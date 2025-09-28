package com.akn.ns.neighbour_snack_be.dto;

import java.util.List;

public record PaginationResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isLast,
        String sortBy,
        String sortDir
) {
}
