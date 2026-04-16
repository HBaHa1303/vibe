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
        @DisplayName("should create active user with all required fields populated")
        void shouldCreateActiveUserWithAllFields() {
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
        }

        @Test
        @DisplayName("should create user with null optional fields")
        void shouldCreateUserWithNullOptionals() {
            var user = User.create(validUsername, validEmail, null, null, null, null, UserRole.USER);

            assertThat(user.getFullName()).isNull();
            assertThat(user.getPhone()).isNull();
            assertThat(user.getAddress()).isNull();
            assertThat(user.getAvatar()).isNull();
        }

        @Test
        @DisplayName("should set createdAt and updatedAt to same timestamp")
        void shouldSetCreatedAtAndUpdatedAtEqually() {
            var before = LocalDateTime.now();
            var user = User.create(validUsername, validEmail, "John", null, null, null, UserRole.USER);
            var after = LocalDateTime.now();

            assertThat(user.getCreatedAt()).isEqualTo(user.getUpdatedAt());
            assertThat(user.getCreatedAt()).isBetween(before, after);
        }

        @Test
        @DisplayName("should generate unique id for each user")
        void shouldGenerateUniqueId() {
            var user1 = User.create(validUsername, validEmail, "A", null, null, null, UserRole.USER);
            var user2 = User.create(Username.of("jane_doe"), Email.of("jane@example.com"), "B", null, null, null, UserRole.USER);

            assertThat(user1.getId()).isNotEqualTo(user2.getId());
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
        @DisplayName("should create user with ADMIN role")
        void shouldCreateWithAdminRole() {
            var user = User.create(validUsername, validEmail, "Admin", null, null, null, UserRole.ADMIN);

            assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
        }
    }

    @Nested
    @DisplayName("User.reconstitute()")
    class Reconstitute {

        @Test
        @DisplayName("should rebuild user preserving all fields exactly")
        void shouldRebuildUserWithAllFields() {
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

        @Test
        @DisplayName("should not validate or mutate any field")
        void shouldNotValidateFields() {
            var id = UUID.randomUUID();

            var user = User.reconstitute(id, null, null, null, null, null, null, null, false, null, null, null);

            assertThat(user.getId()).isEqualTo(id);
            assertThat(user.getUsername()).isNull();
            assertThat(user.getEmail()).isNull();
        }
    }

    @Nested
    @DisplayName("updateProfile()")
    class UpdateProfile {

        @Test
        @DisplayName("should update all profile fields and change updatedAt")
        void shouldUpdateAllProfileFields() {
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
        @DisplayName("should not change id, role, isActive, or createdAt")
        void shouldNotChangeImmutableFields() {
            var user = User.create(validUsername, validEmail, "Old", null, null, null, UserRole.USER);
            var originalId = user.getId();
            var originalCreatedAt = user.getCreatedAt();

            user.updateProfile(Username.of("updated"), Email.of("updated@test.com"), "New", null, null, null);

            assertThat(user.getId()).isEqualTo(originalId);
            assertThat(user.getRole()).isEqualTo(UserRole.USER);
            assertThat(user.isActive()).isTrue();
            assertThat(user.getCreatedAt()).isEqualTo(originalCreatedAt);
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
        @DisplayName("should set isActive to false and update updatedAt")
        void shouldSetInactiveAndUpdateTimestamp() {
            var user = User.create(validUsername, validEmail, "John", null, null, null, UserRole.USER);
            var updatedAtBefore = user.getUpdatedAt();

            user.deactivate();

            assertThat(user.isActive()).isFalse();
            assertThat(user.getUpdatedAt()).isAfterOrEqualTo(updatedAtBefore);
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

        @Test
        @DisplayName("should not change any other field")
        void shouldNotChangeOtherFields() {
            var user = User.create(validUsername, validEmail, "John", "123", "Addr", "av.png", UserRole.ADMIN);
            var originalId = user.getId();
            var originalCreatedAt = user.getCreatedAt();

            user.deactivate();

            assertThat(user.getId()).isEqualTo(originalId);
            assertThat(user.getUsername()).isEqualTo(validUsername);
            assertThat(user.getEmail()).isEqualTo(validEmail);
            assertThat(user.getFullName()).isEqualTo("John");
            assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
            assertThat(user.getCreatedAt()).isEqualTo(originalCreatedAt);
        }
    }

    @Nested
    @DisplayName("recordLogin()")
    class RecordLogin {

        @Test
        @DisplayName("should set lastLoginAt from null to a value and update updatedAt")
        void shouldSetLastLoginAtFromNull() {
            var user = User.create(validUsername, validEmail, "John", null, null, null, UserRole.USER);
            assertThat(user.getLastLoginAt()).isNull();
            var updatedAtBefore = user.getUpdatedAt();

            user.recordLogin();

            assertThat(user.getLastLoginAt()).isNotNull();
            assertThat(user.getUpdatedAt()).isAfterOrEqualTo(updatedAtBefore);
        }

        @Test
        @DisplayName("should update lastLoginAt on subsequent calls")
        void shouldUpdateLastLoginOnSubsequentCalls() throws InterruptedException {
            var user = User.create(validUsername, validEmail, "John", null, null, null, UserRole.USER);

            user.recordLogin();
            var firstLogin = user.getLastLoginAt();

            Thread.sleep(10);
            user.recordLogin();

            assertThat(user.getLastLoginAt()).isAfter(firstLogin);
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("should be equal when same id regardless of other fields")
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
        @DisplayName("should be reflexive")
        void shouldBeReflexive() {
            var user = User.create(validUsername, validEmail, "John", null, null, null, UserRole.USER);

            assertThat(user).isEqualTo(user);
        }

        @Test
        @DisplayName("should not be equal to null")
        void shouldNotBeEqualToNull() {
            var user = User.create(validUsername, validEmail, "John", null, null, null, UserRole.USER);

            assertThat(user).isNotEqualTo(null);
        }

        @Test
        @DisplayName("should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            var user = User.create(validUsername, validEmail, "John", null, null, null, UserRole.USER);

            assertThat(user).isNotEqualTo("not a user");
        }
    }
}
