package com.hades.user.application.command;

public record CreateUserCommand(
        String username,
        String email,
        String fullName,
        String phone,
        String address,
        String avatar,
        String role
) {
}
