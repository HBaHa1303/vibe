package com.hades.user.domain.model;

import com.hades.user.domain.exception.InvalidEmailException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Nested
    @DisplayName("Email.of()")
    class Of {

        @Test
        @DisplayName("should create email with valid format")
        void shouldCreateEmailWithValidFormat() {
            var email = Email.of("User@Example.COM");

            assertThat(email.getValue()).isEqualTo("user@example.com");
        }

        @Test
        @DisplayName("should throw when value is null")
        void shouldThrowWhenNull() {
            assertThatThrownBy(() -> Email.of(null))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessage("Email must not be null or blank");
        }

        @Test
        @DisplayName("should throw when value is blank")
        void shouldThrowWhenBlank() {
            assertThatThrownBy(() -> Email.of("   "))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessage("Email must not be null or blank");
        }

        @Test
        @DisplayName("should throw when format is invalid - no @")
        void shouldThrowWhenNoAtSymbol() {
            assertThatThrownBy(() -> Email.of("invalid-email"))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessage("Invalid email format: invalid-email");
        }

        @Test
        @DisplayName("should throw when format is invalid - no domain")
        void shouldThrowWhenNoDomain() {
            assertThatThrownBy(() -> Email.of("user@"))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessage("Invalid email format: user@");
        }

        @Test
        @DisplayName("should throw when format is invalid - no TLD")
        void shouldThrowWhenNoTld() {
            assertThatThrownBy(() -> Email.of("user@example"))
                    .isInstanceOf(InvalidEmailException.class)
                    .hasMessage("Invalid email format: user@example");
        }

        @Test
        @DisplayName("should trim and lowercase the value")
        void shouldTrimAndLowercase() {
            var email = Email.of("  John@Gmail.COM  ");

            assertThat(email.getValue()).isEqualTo("john@gmail.com");
        }
    }

    @Nested
    @DisplayName("Email.reconstitute()")
    class Reconstitute {

        @Test
        @DisplayName("should create email without validation")
        void shouldCreateWithoutValidation() {
            var email = Email.reconstitute("ANY-raw-value");

            assertThat(email.getValue()).isEqualTo("ANY-raw-value");
        }

        @Test
        @DisplayName("should accept null value")
        void shouldAcceptNull() {
            var email = Email.reconstitute(null);

            assertThat(email.getValue()).isNull();
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("should be equal when values are the same")
        void shouldBeEqualWhenSameValue() {
            var email1 = Email.of("test@example.com");
            var email2 = Email.of("test@example.com");

            assertThat(email1).isEqualTo(email2);
            assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when values differ")
        void shouldNotBeEqualWhenDifferentValue() {
            var email1 = Email.of("a@example.com");
            var email2 = Email.of("b@example.com");

            assertThat(email1).isNotEqualTo(email2);
        }

        @Test
        @DisplayName("should not be equal to null")
        void shouldNotBeEqualToNull() {
            var email = Email.of("test@example.com");

            assertThat(email).isNotEqualTo(null);
        }

        @Test
        @DisplayName("should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            var email = Email.of("test@example.com");

            assertThat(email).isNotEqualTo("test@example.com");
        }
    }

    @Test
    @DisplayName("toString should return the value")
    void toStringShouldReturnValue() {
        var email = Email.of("test@example.com");

        assertThat(email.toString()).isEqualTo("test@example.com");
    }
}
