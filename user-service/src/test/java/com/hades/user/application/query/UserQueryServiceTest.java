package com.hades.user.application.query;

import com.hades.common.model.PageResult;
import com.hades.user.application.dto.UserResult;
import com.hades.user.domain.model.UserRole;
import com.hades.user.infrastructure.persistence.entity.UserJpaEntity;
import com.hades.user.application.query.UserQueryMapperImpl;
import com.hades.user.infrastructure.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    private UserQueryService service;

    @BeforeEach
    void setUp() {
        service = new UserQueryService(userJpaRepository, new UserQueryMapperImpl());
    }

    private UserJpaEntity createJpaEntity() {
        return UserJpaEntity.builder()
                .id(UUID.randomUUID())
                .username("john_doe")
                .email("john@example.com")
                .fullName("John Doe")
                .phone("123456")
                .address("123 Street")
                .avatar("avatar.png")
                .role(UserRole.USER)
                .isActive(true)
                .lastLoginAt(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("getUserById()")
    class GetUserById {

        @Test
        @DisplayName("should return user response when found")
        void shouldReturnUserWhenFound() {
            var entity = createJpaEntity();
            when(userJpaRepository.findById(entity.getId())).thenReturn(Optional.of(entity));

            var result = service.getUserById(entity.getId());

            assertThat(result.id()).isEqualTo(entity.getId());
            assertThat(result.username()).isEqualTo("john_doe");
            assertThat(result.email()).isEqualTo("john@example.com");
            assertThat(result.fullName()).isEqualTo("John Doe");
            assertThat(result.phone()).isEqualTo("123456");
            assertThat(result.address()).isEqualTo("123 Street");
            assertThat(result.avatar()).isEqualTo("avatar.png");
            assertThat(result.role()).isEqualTo("USER");
            assertThat(result.isActive()).isTrue();
        }

        @Test
        @DisplayName("should throw when user not found")
        void shouldThrowWhenNotFound() {
            var id = UUID.randomUUID();
            when(userJpaRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getUserById(id))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("User not found with id: " + id);
        }
    }

    @Nested
    @DisplayName("getAllUsers()")
    class GetAllUsers {

        @Test
        @DisplayName("should return paginated response")
        void shouldReturnPaginatedResponse() {
            var entity = createJpaEntity();
            var pageRequest = PageRequest.of(0, 20);
            Page<UserJpaEntity> jpaPage = new PageImpl<>(List.of(entity), pageRequest, 1);

            when(userJpaRepository.findAll(pageRequest)).thenReturn(jpaPage);

            PageResult<UserResult> result = service.getAllUsers(0, 20);

            assertThat(result.data()).hasSize(1);
            assertThat(result.page()).isEqualTo(0);
            assertThat(result.size()).isEqualTo(20);
            assertThat(result.totalElements()).isEqualTo(1);
            assertThat(result.totalPages()).isEqualTo(1);
        }

        @Test
        @DisplayName("should return empty page when no users")
        void shouldReturnEmptyPage() {
            var pageRequest = PageRequest.of(0, 20);
            Page<UserJpaEntity> emptyPage = new PageImpl<>(List.of(), pageRequest, 0);

            when(userJpaRepository.findAll(pageRequest)).thenReturn(emptyPage);

            PageResult<UserResult> result = service.getAllUsers(0, 20);

            assertThat(result.data()).isEmpty();
            assertThat(result.totalElements()).isZero();
        }
    }
}
