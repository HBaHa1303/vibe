package com.hades.user.presentation.dto;

public record UpdateUserRequest(
        String username,
        String email,
        String fullName,
        String phone,
        String address,
        String avatar
) {
}
