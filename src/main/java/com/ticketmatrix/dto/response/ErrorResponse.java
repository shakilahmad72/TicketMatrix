package com.ticketmatrix.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int statusCode,
        String error,
        String message,
        String path,
        LocalDateTime timestamp,
        Map<String, String> fieldErrors
) {
    public ErrorResponse(int statusCode, String error, String message, String path) {
        this(statusCode, error, message, path, LocalDateTime.now(), null);
    }

    public ErrorResponse(int statusCode, String error, String message, String path, Map<String, String> fieldErrors) {
        this(statusCode, error, message, path, LocalDateTime.now(), fieldErrors);
    }
}
