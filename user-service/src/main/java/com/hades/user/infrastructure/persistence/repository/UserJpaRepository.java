package com.hades.user.infrastructure.persistence.repository;

import com.hades.user.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
