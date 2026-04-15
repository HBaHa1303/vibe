package com.hades.user.domain.model;

import com.hades.user.domain.exception.InvalidUsernameException;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Username {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 50;

    private final String value;

    private Username(String value) {
        this.value = value;
    }

    public static Username of(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidUsernameException("Username must not be null or blank");
        }
        String trimmed = value.trim();
        if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH) {
            throw new InvalidUsernameException(
                    "Username must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters"
            );
        }
        if (!USERNAME_PATTERN.matcher(trimmed).matches()) {
            throw new InvalidUsernameException(
                    "Username must contain only alphanumeric characters and underscores"
            );
        }
        return new Username(trimmed);
    }

    public static Username reconstitute(String value) {
        return new Username(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Username username)) return false;
        return Objects.equals(value, username.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
