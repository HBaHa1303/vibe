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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserCommandServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeleteUserCommandService service;

    private final UUID userId = UUID.randomUUID();
    private final User activeUser = User.reconstitute(
            userId,
            Username.reconstitute("john_doe"),
            Email.reconstitute("john@example.com"),
            "John Doe", null, null, null,
            UserRole.USER, true, null, null, null
    );

    @Nested
    @DisplayName("execute()")
    class Execute {

        @Test
        @DisplayName("should deactivate user successfully")
        void shouldDeactivateSuccessfully() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(activeUser));
            when(userRepository.save(any(User.class))).thenReturn(activeUser);

            assertThatCode(() -> service.execute(userId)).doesNotThrowAnyException();

            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should throw when user not found")
        void shouldThrowWhenNotFound() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.execute(userId))
                    .isInstanceOf(UserDomainException.class)
                    .hasMessage("User not found with id: " + userId);

            verify(userRepository, never()).save(any());
        }
    }
}
