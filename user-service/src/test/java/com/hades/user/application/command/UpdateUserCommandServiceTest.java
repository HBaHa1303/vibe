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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
        @DisplayName("should update user and save with correct new values")
        void shouldUpdateUserWithCorrectValues() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(userRepository.existsByUsername("new_name")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            var command = new UpdateUserCommand(userId, "new_name", "new@example.com",
                    "New Name", "456", "New Address", "new.png");
            service.execute(command);

            var userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            var saved = userCaptor.getValue();

            assertThat(saved.getUsername().getValue()).isEqualTo("new_name");
            assertThat(saved.getEmail().getValue()).isEqualTo("new@example.com");
            assertThat(saved.getFullName()).isEqualTo("New Name");
            assertThat(saved.getPhone()).isEqualTo("456");
            assertThat(saved.getAddress()).isEqualTo("New Address");
            assertThat(saved.getAvatar()).isEqualTo("new.png");
            assertThat(saved.getRole()).isEqualTo(UserRole.USER);
            assertThat(saved.isActive()).isTrue();
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
        @DisplayName("should throw when new email already belongs to another user")
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
        @DisplayName("should throw when new username already belongs to another user")
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
        @DisplayName("should allow update when keeping same email and same username")
        void shouldAllowKeepingSameEmailAndUsername() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            var command = new UpdateUserCommand(userId, "john_doe", "john@example.com",
                    "Updated Name", null, null, null);
            service.execute(command);

            var userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getFullName()).isEqualTo("Updated Name");
            assertThat(userCaptor.getValue().getUsername().getValue()).isEqualTo("john_doe");
            assertThat(userCaptor.getValue().getEmail().getValue()).isEqualTo("john@example.com");
        }

        @Test
        @DisplayName("should allow update when keeping same username but changing email")
        void shouldAllowSameUsernameWithNewEmail() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            var command = new UpdateUserCommand(userId, "john_doe", "new@example.com",
                    "Updated Name", null, null, null);
            service.execute(command);

            var userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getEmail().getValue()).isEqualTo("new@example.com");
            assertThat(userCaptor.getValue().getUsername().getValue()).isEqualTo("john_doe");
        }
    }
}
