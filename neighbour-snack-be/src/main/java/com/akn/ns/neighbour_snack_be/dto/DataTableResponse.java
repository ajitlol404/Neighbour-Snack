package com.akn.ns.neighbour_snack_be.dto;

import java.util.List;

public record DataTableResponse<T>(
        int draw,
        long recordsTotal,
        long recordsFiltered,
        List<T> data
) {}
