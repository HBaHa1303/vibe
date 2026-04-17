package com.hades.user.presentation.controller;

import com.hades.common.model.PageResult;
import com.hades.user.application.dto.UserResult;
import com.hades.user.application.query.UserQueryService;
import com.hades.user.presentation.mapper.UserDtoMapperImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserQueryController.class)
@Import(UserDtoMapperImpl.class)
class UserQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserQueryService userQueryService;

    private UserResult createUserResult() {
        return new UserResult(
                UUID.randomUUID(), "john_doe", "john@example.com", "John Doe",
                "123", "Address", "avatar.png", "USER", true,
                null, LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("GET /api/users/{id}")
    class GetUserById {

        @Test
        @DisplayName("should return 200 with user JSON body")
        void shouldReturn200WithUserResponse() throws Exception {
            var result = createUserResult();

            when(userQueryService.getUserById(result.id())).thenReturn(result);

            mockMvc.perform(get("/api/users/{id}", result.id()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(result.id().toString()))
                    .andExpect(jsonPath("$.username").value("john_doe"))
                    .andExpect(jsonPath("$.email").value("john@example.com"))
                    .andExpect(jsonPath("$.fullName").value("John Doe"))
                    .andExpect(jsonPath("$.phone").value("123"))
                    .andExpect(jsonPath("$.address").value("Address"))
                    .andExpect(jsonPath("$.avatar").value("avatar.png"))
                    .andExpect(jsonPath("$.role").value("USER"))
                    .andExpect(jsonPath("$.isActive").value(true))
                    .andExpect(jsonPath("$.lastLoginAt").isEmpty())
                    .andExpect(jsonPath("$.createdAt").isNotEmpty())
                    .andExpect(jsonPath("$.updatedAt").isNotEmpty());
        }

        @Test
        @DisplayName("should return 404 with error body when user not found")
        void shouldReturn404WhenNotFound() throws Exception {
            var id = UUID.randomUUID();
            when(userQueryService.getUserById(id)).thenThrow(
                    new NoSuchElementException("User not found with id: " + id));

            mockMvc.perform(get("/api/users/{id}", id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.detail").value("User not found with id: " + id))
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("GET /api/users")
    class GetAllUsers {

        @Test
        @DisplayName("should return 200 with paginated response containing data and paging")
        void shouldReturn200WithPaginatedResponse() throws Exception {
            var result = createUserResult();
            var pageResult = new PageResult<>(List.of(result), 0, 20, 1, 1);

            when(userQueryService.getAllUsers(0, 20)).thenReturn(pageResult);

            mockMvc.perform(get("/api/users")
                            .param("page", "0")
                            .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].id").value(result.id().toString()))
                    .andExpect(jsonPath("$.data[0].username").value("john_doe"))
                    .andExpect(jsonPath("$.data[0].email").value("john@example.com"))
                    .andExpect(jsonPath("$.data[0].fullName").value("John Doe"))
                    .andExpect(jsonPath("$.data[0].role").value("USER"))
                    .andExpect(jsonPath("$.data[0].isActive").value(true))
                    .andExpect(jsonPath("$.paging.page").value(0))
                    .andExpect(jsonPath("$.paging.size").value(20))
                    .andExpect(jsonPath("$.paging.totalElements").value(1))
                    .andExpect(jsonPath("$.paging.totalPages").value(1));
        }

        @Test
        @DisplayName("should use default pagination params when not provided")
        void shouldUseDefaultPagination() throws Exception {
            PageResult<UserResult> pageResult = new PageResult<>(List.of(), 0, 20, 0, 0);

            when(userQueryService.getAllUsers(0, 20)).thenReturn(pageResult);

            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isEmpty())
                    .andExpect(jsonPath("$.paging.page").value(0))
                    .andExpect(jsonPath("$.paging.size").value(20))
                    .andExpect(jsonPath("$.paging.totalElements").value(0));
        }
    }
}
