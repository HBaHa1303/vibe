package com.hades.user.application.query;

import com.hades.user.application.dto.UserPageResponse;
import com.hades.user.application.dto.UserResponse;
import com.hades.user.infrastructure.persistence.entity.UserJpaEntity;
import com.hades.user.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserJpaRepository userJpaRepository;

    public UserResponse getUserById(UUID id) {
        var entity = userJpaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
        return toResponse(entity);
    }

    public UserPageResponse getAllUsers(int page, int size) {
        Page<UserJpaEntity> jpaPage = userJpaRepository.findAll(PageRequest.of(page, size));
        var responses = jpaPage.getContent().stream().map(this::toResponse).toList();
        return new UserPageResponse(responses, jpaPage.getNumber(), jpaPage.getSize(),
                jpaPage.getTotalElements(), jpaPage.getTotalPages());
    }

    private UserResponse toResponse(UserJpaEntity entity) {
        return new UserResponse(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getFullName(),
                entity.getPhone(),
                entity.getAddress(),
                entity.getAvatar(),
                entity.getRole().name(),
                entity.isActive(),
                entity.getLastLoginAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
