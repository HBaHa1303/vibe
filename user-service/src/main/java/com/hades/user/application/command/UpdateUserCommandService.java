package com.hades.user.application.command;

import com.hades.user.domain.exception.UserDomainException;
import com.hades.user.domain.model.Email;
import com.hades.user.domain.model.Username;
import com.hades.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserCommandService {

    private final UserRepository userRepository;

    @Transactional
    public void execute(UpdateUserCommand command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserDomainException("User not found with id: " + command.userId()));

        var newUsername = Username.of(command.username());
        var newEmail = Email.of(command.email());

        if (!user.getEmail().getValue().equals(newEmail.getValue())
                && userRepository.existsByEmail(newEmail.getValue())) {
            throw new UserDomainException("Email already exists: " + newEmail.getValue());
        }
        if (!user.getUsername().getValue().equals(newUsername.getValue())
                && userRepository.existsByUsername(newUsername.getValue())) {
            throw new UserDomainException("Username already exists: " + newUsername.getValue());
        }

        user.updateProfile(newUsername, newEmail, command.fullName(), command.phone(),
                command.address(), command.avatar());
        userRepository.save(user);
    }
}
