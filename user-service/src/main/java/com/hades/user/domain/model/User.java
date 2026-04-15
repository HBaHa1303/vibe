package com.hades.user.domain.model;

import com.hades.user.domain.exception.UserDomainException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class User {

    private final UUID id;
    private Username username;
    private Email email;
    private String fullName;
    private String phone;
    private String address;
    private String avatar;
    private UserRole role;
    private boolean isActive;
    private LocalDateTime lastLoginAt;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User(UUID id, Username username, Email email, String fullName, String phone,
                 String address, String avatar, UserRole role, boolean isActive,
                 LocalDateTime lastLoginAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.avatar = avatar;
        this.role = role;
        this.isActive = isActive;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User create(Username username, Email email, String fullName, String phone,
                              String address, String avatar, UserRole role) {
        if (username == null) {
            throw new UserDomainException("Username must not be null");
        }
        if (email == null) {
            throw new UserDomainException("Email must not be null");
        }
        if (role == null) {
            throw new UserDomainException("Role must not be null");
        }

        LocalDateTime now = LocalDateTime.now();
        return new User(
                UUID.randomUUID(),
                username,
                email,
                fullName,
                phone,
                address,
                avatar,
                role,
                true,
                null,
                now,
                now
        );
    }

    public static User reconstitute(UUID id, Username username, Email email, String fullName,
                                    String phone, String address, String avatar, UserRole role,
                                    boolean isActive, LocalDateTime lastLoginAt,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new User(id, username, email, fullName, phone, address, avatar,
                role, isActive, lastLoginAt, createdAt, updatedAt);
    }

    public void updateProfile(Username username, Email email, String fullName,
                              String phone, String address, String avatar) {
        if (username == null) {
            throw new UserDomainException("Username must not be null");
        }
        if (email == null) {
            throw new UserDomainException("Email must not be null");
        }
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.avatar = avatar;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        if (!isActive) {
            throw new UserDomainException("User is already deactivated");
        }
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Username getUsername() {
        return username;
    }

    public Email getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getAvatar() {
        return avatar;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
