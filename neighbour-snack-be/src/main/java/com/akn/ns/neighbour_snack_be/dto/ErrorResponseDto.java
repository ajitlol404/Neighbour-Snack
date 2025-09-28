package com.akn.ns.neighbour_snack_be.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public record ErrorResponseDto(
        ZonedDateTime timestamp,
        int statusCode,
        String error,
        String message,
        String path,
        String method,
        String exception,
        Map<String, List<String>> fieldErrors
) {
    // Factory method: General purpose
    public static ErrorResponseDto of(
            int statusCode,
            String error,
            String message,
            String path,
            String method,
            String exception,
            Map<String,
                    List<String>> fieldErrors
    ) {
        return new ErrorResponseDto(
                ZonedDateTime.now(),
                statusCode,
                error,
                message,
                path,
                method,
                exception,
                fieldErrors
        );
    }

    // Factory method: No field errors
    public static ErrorResponseDto of(int statusCode, String error, String message, String path, String method, String exception) {
        return of(statusCode, error, message, path, method, exception, null);
    }
}
