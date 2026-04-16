package com.hades.user.e2e;

import com.hades.user.infrastructure.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Nested
    @DisplayName("Full user lifecycle")
    class FullLifecycle {

        @Test
        @DisplayName("should complete create -> get -> update -> get -> delete flow")
        void shouldCompleteFullLifecycle() throws Exception {
            // Step 1: Create user
            var createBody = """
                    {
                        "username": "lifecycle_user",
                        "email": "lifecycle@test.com",
                        "fullName": "Lifecycle User",
                        "phone": "111",
                        "address": "Test Address",
                        "avatar": "test.png",
                        "role": "USER"
                    }
                    """;

            var createResult = mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createBody))
                    .andExpect(status().isCreated())
                    .andReturn();

            var location = createResult.getResponse().getHeader("Location");
            assertThat(location).isNotNull();
            var userId = location.substring(location.lastIndexOf("/") + 1);

            // Step 2: Get user and verify created data
            mockMvc.perform(get("/api/users/{id}", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("lifecycle_user"))
                    .andExpect(jsonPath("$.email").value("lifecycle@test.com"))
                    .andExpect(jsonPath("$.fullName").value("Lifecycle User"))
                    .andExpect(jsonPath("$.role").value("USER"))
                    .andExpect(jsonPath("$.isActive").value(true));

            // Step 3: Update user
            var updateBody = """
                    {
                        "username": "updated_user",
                        "email": "updated@test.com",
                        "fullName": "Updated Name",
                        "phone": "222",
                        "address": "Updated Address",
                        "avatar": "updated.png"
                    }
                    """;

            mockMvc.perform(put("/api/users/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateBody))
                    .andExpect(status().isNoContent());

            // Step 4: Get updated user and verify changes
            mockMvc.perform(get("/api/users/{id}", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("updated_user"))
                    .andExpect(jsonPath("$.email").value("updated@test.com"))
                    .andExpect(jsonPath("$.fullName").value("Updated Name"));

            // Step 5: Delete user
            mockMvc.perform(delete("/api/users/{id}", userId))
                    .andExpect(status().isNoContent());

            // Step 6: Verify user is deactivated (not deleted, soft delete)
            // The user still exists but is deactivated
            assertThat(userJpaRepository.findById(java.util.UUID.fromString(userId)))
                    .isPresent()
                    .get().satisfies(entity -> assertThat(entity.isActive()).isFalse());
        }
    }

    @Nested
    @DisplayName("Create user validation")
    class CreateUserValidation {

        @Test
        @DisplayName("should reject duplicate email")
        void shouldRejectDuplicateEmail() throws Exception {
            var body = """
                    {
                        "username": "unique_user",
                        "email": "duplicate@test.com",
                        "fullName": "User One",
                        "role": "USER"
                    }
                    """;

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated());

            var duplicateBody = """
                    {
                        "username": "another_user",
                        "email": "duplicate@test.com",
                        "fullName": "User Two",
                        "role": "USER"
                    }
                    """;

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(duplicateBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Email already exists: duplicate@test.com"));
        }

        @Test
        @DisplayName("should reject duplicate username")
        void shouldRejectDuplicateUsername() throws Exception {
            var body = """
                    {
                        "username": "dup_username",
                        "email": "first@test.com",
                        "fullName": "User One",
                        "role": "USER"
                    }
                    """;

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated());

            var duplicateBody = """
                    {
                        "username": "dup_username",
                        "email": "second@test.com",
                        "fullName": "User Two",
                        "role": "USER"
                    }
                    """;

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(duplicateBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Username already exists: dup_username"));
        }

        @Test
        @DisplayName("should reject invalid role")
        void shouldRejectInvalidRole() throws Exception {
            var body = """
                    {
                        "username": "role_user",
                        "email": "role@test.com",
                        "fullName": "Role User",
                        "role": "SUPERADMIN"
                    }
                    """;

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid role: SUPERADMIN"));
        }
    }

    @Nested
    @DisplayName("Pagination")
    class Pagination {

        @Test
        @DisplayName("should return paginated user list")
        void shouldReturnPaginatedList() throws Exception {
            // Create two users
            for (int i = 1; i <= 2; i++) {
                var body = """
                        {
                            "username": "page_user_$i",
                            "email": "page$i@test.com",
                            "fullName": "Page User $i",
                            "role": "USER"
                        }
                        """.replace("$i", String.valueOf(i));

                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                        .andExpect(status().isCreated());
            }

            mockMvc.perform(get("/api/users")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(10));
        }
    }
}
