package com.hades.user.presentation.controller;

import com.hades.common.model.PageResponse;
import com.hades.user.application.query.UserQueryService;
import com.hades.user.presentation.dto.UserDetailResponse;
import com.hades.user.presentation.dto.UserSummaryResponse;
import com.hades.user.presentation.mapper.UserDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserQueryController {

    private final UserQueryService userQueryService;
    private final UserDtoMapper mapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailResponse> getUserById(@PathVariable UUID id) {
        var result = userQueryService.getUserById(id);
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @GetMapping
    public ResponseEntity<PageResponse<UserSummaryResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = userQueryService.getAllUsers(page, size);
        return ResponseEntity.ok(mapper.toPageResponse(result));
    }
}
