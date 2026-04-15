package com.hades.user.application.command;

import com.hades.user.domain.exception.UserDomainException;
import com.hades.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteUserCommandService {

    private final UserRepository userRepository;

    @Transactional
    public void execute(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDomainException("User not found with id: " + userId));
        user.deactivate();
        userRepository.save(user);
    }
}
