package com.hades.user.domain.repository;

import com.hades.user.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID id);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
