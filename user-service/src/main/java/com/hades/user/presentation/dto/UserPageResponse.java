package com.hades.user.presentation.dto;

import java.util.List;

public record UserPageResponse(
        List<UserSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
