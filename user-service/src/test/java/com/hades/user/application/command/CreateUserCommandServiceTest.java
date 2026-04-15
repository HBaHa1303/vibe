package com.hades.user.application.command;

import com.hades.user.domain.exception.UserDomainException;
import com.hades.user.domain.model.User;
import com.hades.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserCommandServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateUserCommandService service;

    private final CreateUserCommand validCommand = new CreateUserCommand(
            "john_doe", "john@example.com", "John Doe", "123456",
            "123 Street", "avatar.png", "USER"
    );

    @Nested
    @DisplayName("execute()")
    class Execute {

        @Test
        @DisplayName("should create user and return id on success")
        void shouldCreateUserSuccessfully() {
            when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
            when(userRepository.existsByUsername("john_doe")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var result = service.execute(validCommand);

            assertThat(result).isNotNull();
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should throw when email already exists")
        void shouldThrowWhenEmailExists() {
            when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

            assertThatThrownBy(() -> service.execute(validCommand))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("Email already exists: john@example.com");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw when username already exists")
        void shouldThrowWhenUsernameExists() {
            when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
            when(userRepository.existsByUsername("john_doe")).thenReturn(true);

            assertThatThrownBy(() -> service.execute(validCommand))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("Username already exists: john_doe");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw when role is invalid")
        void shouldThrowWhenRoleInvalid() {
            var command = new CreateUserCommand(
                    "john_doe", "john@example.com", "John Doe", null, null, null, "INVALID_ROLE"
            );

            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("Invalid role: INVALID_ROLE");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should return saved user id")
        void shouldReturnSavedUserId() {
            var expectedId = UUID.randomUUID();
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                var user = (User) invocation.getArgument(0);
                return User.reconstitute(expectedId, user.getUsername(), user.getEmail(),
                        user.getFullName(), user.getPhone(), user.getAddress(), user.getAvatar(),
                        user.getRole(), user.isActive(), user.getLastLoginAt(),
                        user.getCreatedAt(), user.getUpdatedAt());
            });

            var result = service.execute(validCommand);

            assertThat(result).isEqualTo(expectedId);
        }
    }
}
