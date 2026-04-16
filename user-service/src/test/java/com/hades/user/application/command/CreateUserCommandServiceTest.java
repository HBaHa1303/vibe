package com.hades.user.application.command;

import com.hades.user.domain.exception.UserDomainException;
import com.hades.user.domain.model.User;
import com.hades.user.domain.model.UserRole;
import com.hades.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
        @DisplayName("should create user with correct domain values and return saved id")
        void shouldCreateUserWithCorrectDomainValues() {
            var expectedId = UUID.randomUUID();
            when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
            when(userRepository.existsByUsername("john_doe")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                var user = (User) invocation.getArgument(0);
                return User.reconstitute(expectedId, user.getUsername(), user.getEmail(),
                        user.getFullName(), user.getPhone(), user.getAddress(), user.getAvatar(),
                        user.getRole(), user.isActive(), user.getLastLoginAt(),
                        user.getCreatedAt(), user.getUpdatedAt());
            });

            var result = service.execute(validCommand);

            assertThat(result).isEqualTo(expectedId);

            var userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            var savedUser = userCaptor.getValue();
            assertThat(savedUser.getUsername().getValue()).isEqualTo("john_doe");
            assertThat(savedUser.getEmail().getValue()).isEqualTo("john@example.com");
            assertThat(savedUser.getFullName()).isEqualTo("John Doe");
            assertThat(savedUser.getPhone()).isEqualTo("123456");
            assertThat(savedUser.getAddress()).isEqualTo("123 Street");
            assertThat(savedUser.getAvatar()).isEqualTo("avatar.png");
            assertThat(savedUser.getRole()).isEqualTo(UserRole.USER);
            assertThat(savedUser.isActive()).isTrue();
        }

        @Test
        @DisplayName("should throw when email already exists and not call save")
        void shouldThrowWhenEmailExists() {
            when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

            assertThatThrownBy(() -> service.execute(validCommand))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("Email already exists: john@example.com");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw when username already exists and not call save")
        void shouldThrowWhenUsernameExists() {
            when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
            when(userRepository.existsByUsername("john_doe")).thenReturn(true);

            assertThatThrownBy(() -> service.execute(validCommand))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("Username already exists: john_doe");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw when role is invalid and not call repository")
        void shouldThrowWhenRoleInvalid() {
            var command = new CreateUserCommand(
                    "john_doe", "john@example.com", "John Doe", null, null, null, "INVALID_ROLE"
            );

            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("Invalid role: INVALID_ROLE");

            verify(userRepository, never()).existsByEmail(anyString());
            verify(userRepository, never()).existsByUsername(anyString());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should propagate invalid username error without calling repository")
        void shouldPropagateInvalidUsernameError() {
            var command = new CreateUserCommand(
                    "ab", "john@example.com", "John", null, null, null, "USER"
            );

            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(UserDomainException.class);

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should propagate invalid email error without calling repository")
        void shouldPropagateInvalidEmailError() {
            var command = new CreateUserCommand(
                    "john_doe", "not-an-email", "John", null, null, null, "USER"
            );

            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(UserDomainException.class);

            verify(userRepository, never()).save(any());
        }
    }
}
