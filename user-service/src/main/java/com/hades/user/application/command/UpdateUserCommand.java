package com.hades.user.application.command;

import java.util.UUID;

public record UpdateUserCommand(
        UUID userId,
        String username,
        String email,
        String fullName,
        String phone,
        String address,
        String avatar
) {
}
