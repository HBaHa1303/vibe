package com.hades.user.application.command;

import com.hades.user.domain.exception.UserDomainException;
import com.hades.user.domain.model.Email;
import com.hades.user.domain.model.User;
import com.hades.user.domain.model.UserRole;
import com.hades.user.domain.model.Username;
import com.hades.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserCommandServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UpdateUserCommandService service;

    private final UUID userId = UUID.randomUUID();
    private final User existingUser = User.reconstitute(
            userId,
            Username.reconstitute("john_doe"),
            Email.reconstitute("john@example.com"),
            "John Doe", "123", "Address", "avatar.png",
            UserRole.USER, true, null, null, null
    );

    @Nested
    @DisplayName("execute()")
    class Execute {

        @Test
        @DisplayName("should update user successfully")
        void shouldUpdateSuccessfully() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(userRepository.existsByUsername("new_name")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(existingUser);

            var command = new UpdateUserCommand(userId, "new_name", "new@example.com",
                    "New Name", "456", "New Address", "new.png");

            assertThatCode(() -> service.execute(command)).doesNotThrowAnyException();

            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should throw when user not found")
        void shouldThrowWhenNotFound() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            var command = new UpdateUserCommand(userId, "john_doe", "john@example.com",
                    "John", null, null, null);

            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("User not found with id: " + userId);

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw when new email already exists")
        void shouldThrowWhenNewEmailExists() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

            var command = new UpdateUserCommand(userId, "john_doe", "taken@example.com",
                    "John", null, null, null);

            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("Email already exists: taken@example.com");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw when new username already exists")
        void shouldThrowWhenNewUsernameExists() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByUsername("taken_name")).thenReturn(true);

            var command = new UpdateUserCommand(userId, "taken_name", "john@example.com",
                    "John", null, null, null);

            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("Username already exists: taken_name");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("should allow keeping same email without duplicate check")
        void shouldAllowSameEmail() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.save(any(User.class))).thenReturn(existingUser);

            var command = new UpdateUserCommand(userId, "john_doe", "john@example.com",
                    "Updated Name", null, null, null);

            assertThatCode(() -> service.execute(command)).doesNotThrowAnyException();

            verify(userRepository, never()).existsByEmail(anyString());
            verify(userRepository, never()).existsByUsername(anyString());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should allow keeping same username without duplicate check")
        void shouldAllowSameUsername() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(existingUser);

            var command = new UpdateUserCommand(userId, "john_doe", "new@example.com",
                    "Updated Name", null, null, null);

            assertThatCode(() -> service.execute(command)).doesNotThrowAnyException();

            verify(userRepository, never()).existsByUsername(anyString());
            verify(userRepository).save(any(User.class));
        }
    }
}
