package com.hades.user.presentation.mapper;

import com.hades.user.application.command.CreateUserCommand;
import com.hades.user.application.command.UpdateUserCommand;
import com.hades.user.application.dto.UserResponse;
import com.hades.user.presentation.dto.CreateUserRequest;
import com.hades.user.presentation.dto.UpdateUserRequest;
import com.hades.user.presentation.dto.UserDetailResponse;
import com.hades.user.presentation.dto.UserSummaryResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserDtoMapper {

    public CreateUserCommand toCreateCommand(CreateUserRequest request) {
        return new CreateUserCommand(
                request.username(),
                request.email(),
                request.fullName(),
                request.phone(),
                request.address(),
                request.avatar(),
                request.role()
        );
    }

    public UpdateUserCommand toUpdateCommand(UUID userId, UpdateUserRequest request) {
        return new UpdateUserCommand(
                userId,
                request.username(),
                request.email(),
                request.fullName(),
                request.phone(),
                request.address(),
                request.avatar()
        );
    }

    public UserDetailResponse toDetailResponse(UserResponse response) {
        return new UserDetailResponse(
                response.id(),
                response.username(),
                response.email(),
                response.fullName(),
                response.phone(),
                response.address(),
                response.avatar(),
                response.role(),
                response.isActive(),
                response.lastLoginAt(),
                response.createdAt(),
                response.updatedAt()
        );
    }

    public com.hades.user.presentation.dto.UserPageResponse toPageResponse(
            com.hades.user.application.dto.UserPageResponse applicationPage) {
        var summaries = applicationPage.content().stream()
                .map(this::toSummaryResponse)
                .toList();
        return new com.hades.user.presentation.dto.UserPageResponse(
                summaries,
                applicationPage.page(),
                applicationPage.size(),
                applicationPage.totalElements(),
                applicationPage.totalPages()
        );
    }

    private UserSummaryResponse toSummaryResponse(UserResponse response) {
        return new UserSummaryResponse(
                response.id(),
                response.username(),
                response.email(),
                response.fullName(),
                response.role(),
                response.isActive()
        );
    }
}
