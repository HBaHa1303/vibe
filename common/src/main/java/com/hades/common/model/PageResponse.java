package com.hades.common.model;

import java.util.List;

public record PageResponse<T>(
        List<T> data,
        PagingInfo paging
) {
}
