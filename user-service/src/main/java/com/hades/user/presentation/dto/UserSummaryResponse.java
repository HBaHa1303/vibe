package com.hades.user.presentation.dto;

import java.util.UUID;

public record UserSummaryResponse(
        UUID id,
        String username,
        String email,
        String fullName,
        String role,
        boolean isActive
) {
}
