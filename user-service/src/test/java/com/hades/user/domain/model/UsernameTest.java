package com.hades.user.domain.model;

import com.hades.user.domain.exception.InvalidUsernameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsernameTest {

    @Nested
    @DisplayName("Username.of()")
    class Of {

        @Test
        @DisplayName("should create username with valid value")
        void shouldCreateWithValidValue() {
            var username = Username.of("john_doe123");

            assertThat(username.getValue()).isEqualTo("john_doe123");
        }

        @Test
        @DisplayName("should throw when value is null")
        void shouldThrowWhenNull() {
            assertThatThrownBy(() -> Username.of(null))
                    .isInstanceOf(InvalidUsernameException.class)
                    .hasMessage("Username must not be null or blank");
        }

        @Test
        @DisplayName("should throw when value is blank")
        void shouldThrowWhenBlank() {
            assertThatThrownBy(() -> Username.of("   "))
                    .isInstanceOf(InvalidUsernameException.class)
                    .hasMessage("Username must not be null or blank");
        }

        @Test
        @DisplayName("should throw when value is too short")
        void shouldThrowWhenTooShort() {
            assertThatThrownBy(() -> Username.of("ab"))
                    .isInstanceOf(InvalidUsernameException.class)
                    .hasMessage("Username must be between 3 and 50 characters");
        }

        @Test
        @DisplayName("should throw when value is too long")
        void shouldThrowWhenTooLong() {
            var longValue = "a".repeat(51);

            assertThatThrownBy(() -> Username.of(longValue))
                    .isInstanceOf(InvalidUsernameException.class)
                    .hasMessage("Username must be between 3 and 50 characters");
        }

        @Test
        @DisplayName("should accept value at min length boundary")
        void shouldAcceptMinLength() {
            var username = Username.of("abc");

            assertThat(username.getValue()).isEqualTo("abc");
        }

        @Test
        @DisplayName("should accept value at max length boundary")
        void shouldAcceptMaxLength() {
            var value = "a".repeat(50);
            var username = Username.of(value);

            assertThat(username.getValue()).isEqualTo(value);
        }

        @Test
        @DisplayName("should throw when value contains special characters")
        void shouldThrowWhenSpecialChars() {
            assertThatThrownBy(() -> Username.of("john-doe"))
                    .isInstanceOf(InvalidUsernameException.class)
                    .hasMessage("Username must contain only alphanumeric characters and underscores");
        }

        @Test
        @DisplayName("should throw when value contains spaces")
        void shouldThrowWhenSpaces() {
            assertThatThrownBy(() -> Username.of("john doe"))
                    .isInstanceOf(InvalidUsernameException.class)
                    .hasMessage("Username must contain only alphanumeric characters and underscores");
        }

        @Test
        @DisplayName("should trim whitespace")
        void shouldTrimWhitespace() {
            var username = Username.of("  john_doe  ");

            assertThat(username.getValue()).isEqualTo("john_doe");
        }
    }

    @Nested
    @DisplayName("Username.reconstitute()")
    class Reconstitute {

        @Test
        @DisplayName("should create without validation")
        void shouldCreateWithoutValidation() {
            var username = Username.reconstitute("any-value!");

            assertThat(username.getValue()).isEqualTo("any-value!");
        }

        @Test
        @DisplayName("should accept null value")
        void shouldAcceptNull() {
            var username = Username.reconstitute(null);

            assertThat(username.getValue()).isNull();
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("should be equal when values are the same")
        void shouldBeEqualWhenSameValue() {
            var username1 = Username.of("john_doe");
            var username2 = Username.of("john_doe");

            assertThat(username1).isEqualTo(username2);
            assertThat(username1.hashCode()).isEqualTo(username2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when values differ")
        void shouldNotBeEqualWhenDifferentValue() {
            var username1 = Username.of("john_doe");
            var username2 = Username.of("jane_doe");

            assertThat(username1).isNotEqualTo(username2);
        }

        @Test
        @DisplayName("should not be equal to null")
        void shouldNotBeEqualToNull() {
            var username = Username.of("john_doe");

            assertThat(username).isNotEqualTo(null);
        }

        @Test
        @DisplayName("should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            var username = Username.of("john_doe");

            assertThat(username).isNotEqualTo("john_doe");
        }
    }

    @Test
    @DisplayName("toString should return the value")
    void toStringShouldReturnValue() {
        var username = Username.of("john_doe");

        assertThat(username.toString()).isEqualTo("john_doe");
    }
}
