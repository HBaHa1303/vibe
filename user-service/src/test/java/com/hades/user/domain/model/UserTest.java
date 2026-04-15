package com.hades.user.domain.model;

import com.hades.user.domain.exception.UserDomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    private final Username validUsername = Username.of("john_doe");
    private final Email validEmail = Email.of("john@example.com");

    @Nested
    @DisplayName("User.create()")
    class Create {

        @Test
        @DisplayName("should create user with required fields")
        void shouldCreateUser() {
            var user = User.create(validUsername, validEmail, "John Doe", "123456",
                    "123 Street", "avatar.png", UserRole.USER);

            assertThat(user.getId()).isNotNull();
            assertThat(user.getUsername()).isEqualTo(validUsername);
            assertThat(user.getEmail()).isEqualTo(validEmail);
            assertThat(user.getFullName()).isEqualTo("John Doe");
            assertThat(user.getPhone()).isEqualTo("123456");
            assertThat(user.getAddress()).isEqualTo("123 Street");
            assertThat(user.getAvatar()).isEqualTo("avatar.png");
            assertThat(user.getRole()).isEqualTo(UserRole.USER);
            assertThat(user.isActive()).isTrue();
            assertThat(user.getLastLoginAt()).isNull();
            assertThat(user.getCreatedAt()).isNotNull();
            assertThat(user.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("should set createdAt and updatedAt to same value")
        void shouldSetTimestamps() {
            var user = User.create(validUsername, validEmail, "John", null, null, null, UserRole.USER);

            assertThat(user.getCreatedAt()).isEqualTo(user.getUpdatedAt());
        }

        @Test
        @DisplayName("should throw when username is null")
        void shouldThrowWhenUsernameNull() {
            assertThatThrownBy(() -> User.create(null, validEmail, "John", null, null, null, UserRole.USER))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("Username must not be null");
        }

        @Test
        @DisplayName("should throw when email is null")
        void shouldThrowWhenEmailNull() {
            assertThatThrownBy(() -> User.create(validUsername, null, "John", null, null, null, UserRole.USER))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("Email must not be null");
        }

        @Test
        @DisplayName("should throw when role is null")
        void shouldThrowWhenRoleNull() {
            assertThatThrownBy(() -> User.create(validUsername, validEmail, "John", null, null, null, null))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("Role must not be null");
        }

        @Test
        @DisplayName("should create with admin role")
        void shouldCreateWithAdminRole() {
            var user = User.create(validUsername, validEmail, "Admin", null, null, null, UserRole.ADMIN);

            assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
        }
    }

    @Nested
    @DisplayName("User.reconstitute()")
    class Reconstitute {

        @Test
        @DisplayName("should rebuild user with all fields")
        void shouldRebuildUser() {
            var id = UUID.randomUUID();
            var createdAt = LocalDateTime.now().minusDays(1);
            var updatedAt = LocalDateTime.now();
            var lastLoginAt = LocalDateTime.now().minusHours(1);

            var user = User.reconstitute(id, validUsername, validEmail, "John Doe", "123",
                    "Address", "avatar.png", UserRole.ADMIN, false, lastLoginAt, createdAt, updatedAt);

            assertThat(user.getId()).isEqualTo(id);
            assertThat(user.getUsername()).isEqualTo(validUsername);
            assertThat(user.getEmail()).isEqualTo(validEmail);
            assertThat(user.getFullName()).isEqualTo("John Doe");
            assertThat(user.getPhone()).isEqualTo("123");
            assertThat(user.getAddress()).isEqualTo("Address");
            assertThat(user.getAvatar()).isEqualTo("avatar.png");
            assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
            assertThat(user.isActive()).isFalse();
            assertThat(user.getLastLoginAt()).isEqualTo(lastLoginAt);
            assertThat(user.getCreatedAt()).isEqualTo(createdAt);
            assertThat(user.getUpdatedAt()).isEqualTo(updatedAt);
        }
    }

    @Nested
    @DisplayName("updateProfile()")
    class UpdateProfile {

        @Test
        @DisplayName("should update profile fields")
        void shouldUpdateProfile() {
            var user = User.create(validUsername, validEmail, "Old Name", null, null, null, UserRole.USER);
            var updatedAtBefore = user.getUpdatedAt();

            var newUsername = Username.of("new_name");
            var newEmail = Email.of("new@example.com");
            user.updateProfile(newUsername, newEmail, "New Name", "999", "New Address", "new.png");

            assertThat(user.getUsername()).isEqualTo(newUsername);
            assertThat(user.getEmail()).isEqualTo(newEmail);
            assertThat(user.getFullName()).isEqualTo("New Name");
            assertThat(user.getPhone()).isEqualTo("999");
            assertThat(user.getAddress()).isEqualTo("New Address");
            assertThat(user.getAvatar()).isEqualTo("new.png");
            assertThat(user.getUpdatedAt()).isAfterOrEqualTo(updatedAtBefore);
        }

        @Test
        @DisplayName("should throw when username is null")
        void shouldThrowWhenUsernameNull() {
            var user = User.create(validUsername, validEmail, "John", null, null, null, UserRole.USER);

            assertThatThrownBy(() -> user.updateProfile(null, validEmail, "John", null, null, null))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("Username must not be null");
        }

        @Test
        @DisplayName("should throw when email is null")
        void shouldThrowWhenEmailNull() {
            var user = User.create(validUsername, validEmail, "John", null, null, null, UserRole.USER);

            assertThatThrownBy(() -> user.updateProfile(validUsername, null, "John", null, null, null))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("Email must not be null");
        }
    }

    @Nested
    @DisplayName("deactivate()")
    class Deactivate {

        @Test
        @DisplayName("should deactivate active user")
        void shouldDeactivate() {
            var user = User.create(validUsername, validEmail, "John", null, null, null, UserRole.USER);

            user.deactivate();

            assertThat(user.isActive()).isFalse();
            assertThat(user.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("should throw when already deactivated")
        void shouldThrowWhenAlreadyDeactivated() {
            var user = User.create(validUsername, validEmail, "John", null, null, null, UserRole.USER);
            user.deactivate();

            assertThatThrownBy(user::deactivate)
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("User is already deactivated");
        }
    }

    @Nested
    @DisplayName("recordLogin()")
    class RecordLogin {

        @Test
        @DisplayName("should set lastLoginAt and updatedAt")
        void shouldSetLastLoginAt() {
            var user = User.create(validUsername, validEmail, "John", null, null, null, UserRole.USER);
            assertThat(user.getLastLoginAt()).isNull();

            user.recordLogin();

            assertThat(user.getLastLoginAt()).isNotNull();
            assertThat(user.getUpdatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("should be equal when same id")
        void shouldBeEqualWhenSameId() {
            var id = UUID.randomUUID();
            var user1 = User.reconstitute(id, validUsername, validEmail, "A", null, null, null, UserRole.USER, true, null, null, null);
            var user2 = User.reconstitute(id, Username.of("other"), Email.of("other@test.com"), "B", null, null, null, UserRole.ADMIN, false, null, null, null);

            assertThat(user1).isEqualTo(user2);
            assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when different id")
        void shouldNotBeEqualWhenDifferentId() {
            var user1 = User.create(validUsername, validEmail, "John", null, null, null, UserRole.USER);
            var user2 = User.create(validUsername, validEmail, "John", null, null, null, UserRole.USER);

            assertThat(user1).isNotEqualTo(user2);
        }

        @Test
        @DisplayName("should not be equal to null")
        void shouldNotBeEqualToNull() {
            var user = User.create(validUsername, validEmail, "John", null, null, null, UserRole.USER);

            assertThat(user).isNotEqualTo(null);
        }
    }
}
