package com.hades.user.presentation.controller;

import com.hades.user.application.dto.UserPageResponse;
import com.hades.user.application.dto.UserResponse;
import com.hades.user.application.query.UserQueryService;
import com.hades.user.presentation.mapper.UserDtoMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserQueryController.class, excludeAutoConfiguration = {
        HibernateJpaAutoConfiguration.class,
        DataSourceAutoConfiguration.class
})
class UserQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserQueryService userQueryService;

    @MockitoBean
    private UserDtoMapper mapper;

    private UserResponse createUserResponse() {
        return new UserResponse(
                UUID.randomUUID(), "john_doe", "john@example.com", "John Doe",
                "123", "Address", "avatar.png", "USER", true,
                null, LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("GET /api/users/{id}")
    class GetUserById {

        @Test
        @DisplayName("should return 200 with user detail")
        void shouldReturnUserDetail() throws Exception {
            var appResponse = createUserResponse();
            var detailResponse = new com.hades.user.presentation.dto.UserDetailResponse(
                    appResponse.id(), appResponse.username(), appResponse.email(),
                    appResponse.fullName(), appResponse.phone(), appResponse.address(),
                    appResponse.avatar(), appResponse.role(), appResponse.isActive(),
                    appResponse.lastLoginAt(), appResponse.createdAt(), appResponse.updatedAt()
            );

            when(userQueryService.getUserById(appResponse.id())).thenReturn(appResponse);
            when(mapper.toDetailResponse(appResponse)).thenReturn(detailResponse);

            mockMvc.perform(get("/api/users/{id}", appResponse.id()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(appResponse.id().toString()))
                    .andExpect(jsonPath("$.username").value("john_doe"))
                    .andExpect(jsonPath("$.email").value("john@example.com"));
        }
    }

    @Nested
    @DisplayName("GET /api/users")
    class GetAllUsers {

        @Test
        @DisplayName("should return 200 with page response")
        void shouldReturnPageResponse() throws Exception {
            var appResponse = createUserResponse();
            var appPage = new UserPageResponse(List.of(appResponse), 0, 20, 1, 1);
            var summaryResponse = new com.hades.user.presentation.dto.UserSummaryResponse(
                    appResponse.id(), appResponse.username(), appResponse.email(),
                    appResponse.fullName(), appResponse.role(), appResponse.isActive()
            );
            var presentationPage = new com.hades.user.presentation.dto.UserPageResponse(
                    List.of(summaryResponse), 0, 20, 1, 1
            );

            when(userQueryService.getAllUsers(0, 20)).thenReturn(appPage);
            when(mapper.toPageResponse(appPage)).thenReturn(presentationPage);

            mockMvc.perform(get("/api/users")
                            .param("page", "0")
                            .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].username").value("john_doe"))
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(20))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1));
        }

        @Test
        @DisplayName("should use default pagination params")
        void shouldUseDefaultPagination() throws Exception {
            var appPage = new UserPageResponse(List.of(), 0, 20, 0, 0);
            var presentationPage = new com.hades.user.presentation.dto.UserPageResponse(
                    List.of(), 0, 20, 0, 0
            );

            when(userQueryService.getAllUsers(0, 20)).thenReturn(appPage);
            when(mapper.toPageResponse(appPage)).thenReturn(presentationPage);

            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }
    }
}
