package com.hades.user.application.query;

import com.hades.common.model.PageResult;
import com.hades.user.application.dto.UserResult;
import com.hades.user.infrastructure.persistence.entity.UserJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserQueryMapper {

    @Mapping(target = "isActive", source = "active")
    UserResult toResult(UserJpaEntity entity);

    List<UserResult> toResultList(List<UserJpaEntity> entities);

    default PageResult<UserResult> toPageResult(Page<UserJpaEntity> page) {
        return new PageResult<>(
                toResultList(page.getContent()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
