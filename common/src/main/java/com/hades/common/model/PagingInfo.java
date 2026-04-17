package com.hades.common.model;

public record PagingInfo(
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
