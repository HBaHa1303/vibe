package com.hades.user.presentation.mapper;

import com.hades.common.model.PageResponse;
import com.hades.common.model.PageResult;
import com.hades.common.model.PagingInfo;
import com.hades.user.application.command.CreateUserCommand;
import com.hades.user.application.command.UpdateUserCommand;
import com.hades.user.application.dto.UserResult;
import com.hades.user.presentation.dto.CreateUserRequest;
import com.hades.user.presentation.dto.UpdateUserRequest;
import com.hades.user.presentation.dto.UserDetailResponse;
import com.hades.user.presentation.dto.UserSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    CreateUserCommand toCreateCommand(CreateUserRequest request);

    @Mapping(target = "userId", source = "userId")
    UpdateUserCommand toUpdateCommand(UUID userId, UpdateUserRequest request);

    UserDetailResponse toResponse(UserResult result);

    UserSummaryResponse toSummaryResponse(UserResult result);

    default PageResponse<UserSummaryResponse> toPageResponse(PageResult<UserResult> pageResult) {
        var summaries = pageResult.data().stream()
                .map(this::toSummaryResponse)
                .toList();
        var paging = new PagingInfo(
                pageResult.page(),
                pageResult.size(),
                pageResult.totalElements(),
                pageResult.totalPages()
        );
        return new PageResponse<>(summaries, paging);
    }
}
