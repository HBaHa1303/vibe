package com.hades.user.infrastructure.persistence.mapper;

import com.hades.user.domain.model.Email;
import com.hades.user.domain.model.User;
import com.hades.user.domain.model.Username;
import com.hades.user.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserPersistenceMapper {

    public User toDomain(UserJpaEntity entity) {
        return User.reconstitute(
                entity.getId(),
                Username.reconstitute(entity.getUsername()),
                Email.reconstitute(entity.getEmail()),
                entity.getFullName(),
                entity.getPhone(),
                entity.getAddress(),
                entity.getAvatar(),
                entity.getRole(),
                entity.isActive(),
                entity.getLastLoginAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public UserJpaEntity toEntity(User user) {
        return UserJpaEntity.builder()
                .id(user.getId())
                .username(user.getUsername().getValue())
                .email(user.getEmail().getValue())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .avatar(user.getAvatar())
                .role(user.getRole())
                .isActive(user.isActive())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public List<User> toDomainList(List<UserJpaEntity> entities) {
        return entities.stream().map(this::toDomain).toList();
    }
}
