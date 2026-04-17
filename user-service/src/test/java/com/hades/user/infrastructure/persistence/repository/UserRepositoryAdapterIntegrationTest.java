package com.hades.user.infrastructure.persistence.repository;

import com.hades.user.domain.model.Email;
import com.hades.user.domain.model.User;
import com.hades.user.domain.model.UserRole;
import com.hades.user.domain.model.Username;
import com.hades.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import({UserRepositoryAdapter.class, com.hades.user.infrastructure.persistence.mapper.UserPersistenceMapperImpl.class})
class UserRepositoryAdapterIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private User createTestUser() {
        return User.create(
                Username.of("john_doe"),
                Email.of("john@example.com"),
                "John Doe", "123456", "123 Street", "avatar.png",
                UserRole.USER
        );
    }

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        @DisplayName("should persist user and return domain entity with generated id")
        void shouldPersistAndReturnDomainEntity() {
            var user = createTestUser();

            var saved = userRepository.save(user);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getUsername().getValue()).isEqualTo("john_doe");
            assertThat(saved.getEmail().getValue()).isEqualTo("john@example.com");
            assertThat(saved.getFullName()).isEqualTo("John Doe");
            assertThat(saved.getRole()).isEqualTo(UserRole.USER);
            assertThat(saved.isActive()).isTrue();
            assertThat(saved.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("should update existing user on re-save")
        void shouldUpdateExistingUser() {
            var user = createTestUser();
            var saved = userRepository.save(user);

            saved.updateProfile(Username.of("updated"), Email.of("updated@test.com"), "Updated", null, null, null);
            var reSaved = userRepository.save(saved);

            assertThat(reSaved.getUsername().getValue()).isEqualTo("updated");
            assertThat(reSaved.getEmail().getValue()).isEqualTo("updated@test.com");

            var found = userRepository.findById(saved.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getUsername().getValue()).isEqualTo("updated");
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("should return empty when user does not exist")
        void shouldReturnEmptyWhenNotFound() {
            var result = userRepository.findById(UUID.randomUUID());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should return persisted user by id")
        void shouldReturnPersistedUser() {
            var saved = userRepository.save(createTestUser());

            var found = userRepository.findById(saved.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(saved.getId());
            assertThat(found.get().getUsername().getValue()).isEqualTo("john_doe");
            assertThat(found.get().getEmail().getValue()).isEqualTo("john@example.com");
            assertThat(found.get().getFullName()).isEqualTo("John Doe");
            assertThat(found.get().getPhone()).isEqualTo("123456");
            assertThat(found.get().getAddress()).isEqualTo("123 Street");
            assertThat(found.get().getAvatar()).isEqualTo("avatar.png");
            assertThat(found.get().getRole()).isEqualTo(UserRole.USER);
            assertThat(found.get().isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("existsByEmail()")
    class ExistsByEmail {

        @Test
        @DisplayName("should return true when email exists")
        void shouldReturnTrueWhenEmailExists() {
            userRepository.save(createTestUser());

            assertThat(userRepository.existsByEmail("john@example.com")).isTrue();
        }

        @Test
        @DisplayName("should return false when email does not exist")
        void shouldReturnFalseWhenEmailNotExists() {
            assertThat(userRepository.existsByEmail("nobody@example.com")).isFalse();
        }

        @Test
        @DisplayName("should be case-sensitive")
        void shouldBeCaseSensitive() {
            userRepository.save(createTestUser());

            assertThat(userRepository.existsByEmail("John@Example.com")).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByUsername()")
    class ExistsByUsername {

        @Test
        @DisplayName("should return true when username exists")
        void shouldReturnTrueWhenUsernameExists() {
            userRepository.save(createTestUser());

            assertThat(userRepository.existsByUsername("john_doe")).isTrue();
        }

        @Test
        @DisplayName("should return false when username does not exist")
        void shouldReturnFalseWhenUsernameNotExists() {
            assertThat(userRepository.existsByUsername("nobody")).isFalse();
        }
    }

    @Nested
    @DisplayName("Persistence round-trip")
    class RoundTrip {

        @Test
        @DisplayName("should preserve all fields through save -> findById cycle")
        void shouldPreserveAllFields() {
            var user = createTestUser();
            var saved = userRepository.save(user);
            var loaded = userRepository.findById(saved.getId()).orElseThrow();

            assertThat(loaded.getId()).isEqualTo(saved.getId());
            assertThat(loaded.getUsername().getValue()).isEqualTo(saved.getUsername().getValue());
            assertThat(loaded.getEmail().getValue()).isEqualTo(saved.getEmail().getValue());
            assertThat(loaded.getFullName()).isEqualTo(saved.getFullName());
            assertThat(loaded.getPhone()).isEqualTo(saved.getPhone());
            assertThat(loaded.getAddress()).isEqualTo(saved.getAddress());
            assertThat(loaded.getAvatar()).isEqualTo(saved.getAvatar());
            assertThat(loaded.getRole()).isEqualTo(saved.getRole());
            assertThat(loaded.isActive()).isEqualTo(saved.isActive());
            assertThat(loaded.getLastLoginAt()).isEqualTo(saved.getLastLoginAt());
            assertThat(loaded.getCreatedAt()).isEqualTo(saved.getCreatedAt());
            assertThat(loaded.getUpdatedAt()).isEqualTo(saved.getUpdatedAt());
        }

        @Test
        @DisplayName("should reflect deactivated state after save")
        void shouldReflectDeactivatedState() {
            var saved = userRepository.save(createTestUser());

            saved.deactivate();
            userRepository.save(saved);

            var loaded = userRepository.findById(saved.getId()).orElseThrow();
            assertThat(loaded.isActive()).isFalse();
        }

        @Test
        @DisplayName("should reflect login timestamp after recordLogin and save")
        void shouldReflectLoginTimestamp() {
            var saved = userRepository.save(createTestUser());
            assertThat(saved.getLastLoginAt()).isNull();

            saved.recordLogin();
            userRepository.save(saved);

            var loaded = userRepository.findById(saved.getId()).orElseThrow();
            assertThat(loaded.getLastLoginAt()).isNotNull();
        }
    }
}
