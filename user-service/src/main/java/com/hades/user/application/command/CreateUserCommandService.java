package com.hades.user.application.command;

import com.hades.user.domain.exception.UserDomainException;
import com.hades.user.domain.model.Email;
import com.hades.user.domain.model.User;
import com.hades.user.domain.model.UserRole;
import com.hades.user.domain.model.Username;
import com.hades.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateUserCommandService {

    private final UserRepository userRepository;

    @Transactional
    public UUID execute(CreateUserCommand command) {
        var username = Username.of(command.username());
        var email = Email.of(command.email());
        var role = parseRole(command.role());

        if (userRepository.existsByEmail(email.getValue())) {
            throw new UserDomainException("Email already exists: " + email.getValue());
        }
        if (userRepository.existsByUsername(username.getValue())) {
            throw new UserDomainException("Username already exists: " + username.getValue());
        }

        var user = User.create(username, email, command.fullName(), command.phone(),
                command.address(), command.avatar(), role);
        var saved = userRepository.save(user);
        return saved.getId();
    }

    private UserRole parseRole(String role) {
        try {
            return UserRole.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new UserDomainException("Invalid role: " + role);
        }
    }
}
