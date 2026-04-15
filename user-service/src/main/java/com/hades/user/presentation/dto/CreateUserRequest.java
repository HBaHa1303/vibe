package com.hades.user.presentation.dto;

public record CreateUserRequest(
        String username,
        String email,
        String fullName,
        String phone,
        String address,
        String avatar,
        String role
) {
}
