package com.hades.user.presentation.controller;

import com.hades.user.application.command.CreateUserCommandService;
import com.hades.user.application.command.DeleteUserCommandService;
import com.hades.user.application.command.UpdateUserCommandService;
import com.hades.user.application.command.CreateUserCommand;
import com.hades.user.presentation.dto.CreateUserRequest;
import com.hades.user.presentation.dto.UpdateUserRequest;
import com.hades.user.presentation.mapper.UserDtoMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserCommandController.class)
class UserCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateUserCommandService createUserCommandService;

    @MockitoBean
    private UpdateUserCommandService updateUserCommandService;

    @MockitoBean
    private DeleteUserCommandService deleteUserCommandService;

    @MockitoBean
    private UserDtoMapper mapper;

    @Nested
    @DisplayName("POST /api/users")
    class CreateUser {

        @Test
        @DisplayName("should return 201 Created with Location header")
        void shouldReturn201Created() throws Exception {
            var userId = UUID.randomUUID();
            var request = new CreateUserRequest("john_doe", "john@example.com",
                    "John Doe", "123", "Address", "avatar.png", "USER");

            var command = new CreateUserCommand("john_doe", "john@example.com",
                    "John Doe", "123", "Address", "avatar.png", "USER");
            when(mapper.toCreateCommand(any(CreateUserRequest.class)))
                    .thenReturn(command);
            when(createUserCommandService.execute(any(CreateUserCommand.class))).thenReturn(userId);

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/users/" + userId));
        }
    }

    @Nested
    @DisplayName("PUT /api/users/{id}")
    class UpdateUser {

        @Test
        @DisplayName("should return 204 No Content")
        void shouldReturn204NoContent() throws Exception {
            var userId = UUID.randomUUID();
            var request = new UpdateUserRequest("new_name", "new@example.com",
                    "New Name", "456", "New Address", "new.png");

            mockMvc.perform(put("/api/users/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());

            verify(updateUserCommandService).execute(any());
        }
    }

    @Nested
    @DisplayName("DELETE /api/users/{id}")
    class DeleteUser {

        @Test
        @DisplayName("should return 204 No Content")
        void shouldReturn204NoContent() throws Exception {
            var userId = UUID.randomUUID();

            mockMvc.perform(delete("/api/users/{id}", userId))
                    .andExpect(status().isNoContent());

            verify(deleteUserCommandService).execute(userId);
        }
    }
}
