package com.hades.user.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResult(
        UUID id,
        String username,
        String email,
        String fullName,
        String phone,
        String address,
        String avatar,
        String role,
        boolean isActive,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
