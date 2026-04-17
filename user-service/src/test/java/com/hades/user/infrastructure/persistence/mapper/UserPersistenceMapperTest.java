package com.hades.user.infrastructure.persistence.mapper;

import com.hades.user.domain.model.Email;
import com.hades.user.domain.model.User;
import com.hades.user.domain.model.UserRole;
import com.hades.user.domain.model.Username;
import com.hades.user.infrastructure.persistence.entity.UserJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserPersistenceMapperTest {

    private final UserPersistenceMapper mapper = new UserPersistenceMapperImpl();

    private User createDomainUser() {
        return User.reconstitute(
                UUID.randomUUID(),
                Username.reconstitute("john_doe"),
                Email.reconstitute("john@example.com"),
                "John Doe", "123", "Address", "avatar.png",
                UserRole.USER, true,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
    }

    private UserJpaEntity createJpaEntity() {
        return UserJpaEntity.builder()
                .id(UUID.randomUUID())
                .username("john_doe")
                .email("john@example.com")
                .fullName("John Doe")
                .phone("123")
                .address("Address")
                .avatar("avatar.png")
                .role(UserRole.USER)
                .isActive(true)
                .lastLoginAt(LocalDateTime.now().minusHours(1))
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("toEntity()")
    class ToEntity {

        @Test
        @DisplayName("should map all domain fields to JPA entity")
        void shouldMapAllFieldsToEntity() {
            var user = createDomainUser();

            var entity = mapper.toEntity(user);

            assertThat(entity.getId()).isEqualTo(user.getId());
            assertThat(entity.getUsername()).isEqualTo("john_doe");
            assertThat(entity.getEmail()).isEqualTo("john@example.com");
            assertThat(entity.getFullName()).isEqualTo("John Doe");
            assertThat(entity.getPhone()).isEqualTo("123");
            assertThat(entity.getAddress()).isEqualTo("Address");
            assertThat(entity.getAvatar()).isEqualTo("avatar.png");
            assertThat(entity.getRole()).isEqualTo(UserRole.USER);
            assertThat(entity.isActive()).isTrue();
            assertThat(entity.getLastLoginAt()).isEqualTo(user.getLastLoginAt());
            assertThat(entity.getCreatedAt()).isEqualTo(user.getCreatedAt());
            assertThat(entity.getUpdatedAt()).isEqualTo(user.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("toDomain()")
    class ToDomain {

        @Test
        @DisplayName("should map all JPA fields to domain entity")
        void shouldMapAllFieldsToDomain() {
            var entity = createJpaEntity();

            var user = mapper.toDomain(entity);

            assertThat(user.getId()).isEqualTo(entity.getId());
            assertThat(user.getUsername().getValue()).isEqualTo("john_doe");
            assertThat(user.getEmail().getValue()).isEqualTo("john@example.com");
            assertThat(user.getFullName()).isEqualTo("John Doe");
            assertThat(user.getPhone()).isEqualTo("123");
            assertThat(user.getAddress()).isEqualTo("Address");
            assertThat(user.getAvatar()).isEqualTo("avatar.png");
            assertThat(user.getRole()).isEqualTo(UserRole.USER);
            assertThat(user.isActive()).isTrue();
            assertThat(user.getLastLoginAt()).isEqualTo(entity.getLastLoginAt());
            assertThat(user.getCreatedAt()).isEqualTo(entity.getCreatedAt());
            assertThat(user.getUpdatedAt()).isEqualTo(entity.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Round-trip")
    class RoundTrip {

        @Test
        @DisplayName("should preserve all fields after domain -> entity -> domain conversion")
        void shouldPreserveAllFieldsInRoundTrip() {
            var original = createDomainUser();

            var entity = mapper.toEntity(original);
            var restored = mapper.toDomain(entity);

            assertThat(restored.getId()).isEqualTo(original.getId());
            assertThat(restored.getUsername().getValue()).isEqualTo(original.getUsername().getValue());
            assertThat(restored.getEmail().getValue()).isEqualTo(original.getEmail().getValue());
            assertThat(restored.getFullName()).isEqualTo(original.getFullName());
            assertThat(restored.getPhone()).isEqualTo(original.getPhone());
            assertThat(restored.getAddress()).isEqualTo(original.getAddress());
            assertThat(restored.getAvatar()).isEqualTo(original.getAvatar());
            assertThat(restored.getRole()).isEqualTo(original.getRole());
            assertThat(restored.isActive()).isEqualTo(original.isActive());
            assertThat(restored.getLastLoginAt()).isEqualTo(original.getLastLoginAt());
            assertThat(restored.getCreatedAt()).isEqualTo(original.getCreatedAt());
            assertThat(restored.getUpdatedAt()).isEqualTo(original.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("toDomainList()")
    class ToDomainList {

        @Test
        @DisplayName("should map list of entities to list of domain users")
        void shouldMapListOfEntities() {
            var entities = List.of(createJpaEntity(), createJpaEntity());

            var users = mapper.toDomainList(entities);

            assertThat(users).hasSize(2);
            assertThat(users.getFirst().getId()).isEqualTo(entities.getFirst().getId());
            assertThat(users.getLast().getId()).isEqualTo(entities.getLast().getId());
        }

        @Test
        @DisplayName("should return empty list for empty input")
        void shouldReturnEmptyList() {
            var users = mapper.toDomainList(List.of());

            assertThat(users).isEmpty();
        }
    }
}
