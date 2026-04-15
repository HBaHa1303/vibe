package com.hades.user.presentation.controller;

import com.hades.user.application.command.CreateUserCommandService;
import com.hades.user.application.command.DeleteUserCommandService;
import com.hades.user.application.command.UpdateUserCommandService;
import com.hades.user.presentation.dto.CreateUserRequest;
import com.hades.user.presentation.dto.UpdateUserRequest;
import com.hades.user.presentation.mapper.UserDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserCommandController {

    private final CreateUserCommandService createUserCommandService;
    private final UpdateUserCommandService updateUserCommandService;
    private final DeleteUserCommandService deleteUserCommandService;
    private final UserDtoMapper mapper;

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody CreateUserRequest request) {
        var command = mapper.toCreateCommand(request);
        var userId = createUserCommandService.execute(command);
        return ResponseEntity.created(URI.create("/api/users/" + userId)).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable UUID id, @RequestBody UpdateUserRequest request) {
        var command = mapper.toUpdateCommand(id, request);
        updateUserCommandService.execute(command);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        deleteUserCommandService.execute(id);
        return ResponseEntity.noContent().build();
    }
}
