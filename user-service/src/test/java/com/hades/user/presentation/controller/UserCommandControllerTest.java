package com.hades.user.presentation.controller;

import com.hades.user.application.command.CreateUserCommandService;
import com.hades.user.application.command.DeleteUserCommandService;
import com.hades.user.application.command.UpdateUserCommandService;
import com.hades.user.domain.exception.UserDomainException;
import com.hades.user.presentation.mapper.UserDtoMapperImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
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
@Import(UserDtoMapperImpl.class)
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

    @Nested
    @DisplayName("POST /api/users")
    class CreateUser {

        @Test
        @DisplayName("should return 201 Created with Location header containing user id")
        void shouldReturn201CreatedWithLocation() throws Exception {
            var userId = UUID.randomUUID();
            var request = new com.hades.user.presentation.dto.CreateUserRequest(
                    "john_doe", "john@example.com", "John Doe", "123", "Address", "avatar.png", "USER");

            when(createUserCommandService.execute(any())).thenReturn(userId);

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/users/" + userId))
                    .andExpect(jsonPath("$").doesNotExist());
        }

        @Test
        @DisplayName("should return 400 with error body when service throws DomainException")
        void shouldReturn400OnDomainException() throws Exception {
            var request = new com.hades.user.presentation.dto.CreateUserRequest(
                    "john_doe", "john@example.com", "John Doe", "123", "Address", "avatar.png", "USER");

            when(createUserCommandService.execute(any()))
                    .thenThrow(new UserDomainException("Email already exists: john@example.com"));

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.detail").value("Email already exists: john@example.com"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("should return 400 when request body is missing")
        void shouldReturn400WhenBodyMissing() throws Exception {
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/users/{id}")
    class UpdateUser {

        @Test
        @DisplayName("should return 204 No Content on successful update")
        void shouldReturn204NoContent() throws Exception {
            var userId = UUID.randomUUID();
            var request = new com.hades.user.presentation.dto.UpdateUserRequest(
                    "new_name", "new@example.com", "New Name", "456", "New Address", "new.png");

            mockMvc.perform(put("/api/users/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("should return 400 with error body when service throws DomainException")
        void shouldReturn400OnDomainException() throws Exception {
            var userId = UUID.randomUUID();
            var request = new com.hades.user.presentation.dto.UpdateUserRequest(
                    "new_name", "new@example.com", "New Name", "456", "New Address", "new.png");

            doThrow(new UserDomainException("Username already exists: new_name"))
                    .when(updateUserCommandService).execute(any());

            mockMvc.perform(put("/api/users/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.detail").value("Username already exists: new_name"))
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("should return 400 when path variable is not a valid UUID")
        void shouldReturn400WhenInvalidUUID() throws Exception {
            var request = new com.hades.user.presentation.dto.UpdateUserRequest(
                    "new_name", "new@example.com", "New Name", "456", "New Address", "new.png");

            mockMvc.perform(put("/api/users/{id}", "not-a-uuid")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
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
        }

        @Test
        @DisplayName("should return 400 with error body when service throws DomainException")
        void shouldReturn400OnDomainException() throws Exception {
            var userId = UUID.randomUUID();
            doThrow(new UserDomainException("User not found with id: " + userId))
                    .when(deleteUserCommandService).execute(userId);

            mockMvc.perform(delete("/api/users/{id}", userId))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.detail").value("User not found with id: " + userId))
                    .andExpect(jsonPath("$.status").value(400));
        }
    }
}
