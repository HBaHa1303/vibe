package com.hades.user.infrastructure.persistence.mapper;

import com.hades.user.domain.model.Email;
import com.hades.user.domain.model.User;
import com.hades.user.domain.model.Username;
import com.hades.user.infrastructure.persistence.entity.UserJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    default User toDomain(UserJpaEntity entity) {
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

    @Mapping(target = "username", source = "username", qualifiedByName = "fromUsername")
    @Mapping(target = "email", source = "email", qualifiedByName = "fromEmail")
    @Mapping(target = "isActive", source = "active")
    UserJpaEntity toEntity(User user);

    default List<User> toDomainList(List<UserJpaEntity> entities) {
        return entities.stream().map(this::toDomain).toList();
    }

    @Named("fromUsername")
    default String fromUsername(Username username) {
        return username.getValue();
    }

    @Named("fromEmail")
    default String fromEmail(Email email) {
        return email.getValue();
    }
}
