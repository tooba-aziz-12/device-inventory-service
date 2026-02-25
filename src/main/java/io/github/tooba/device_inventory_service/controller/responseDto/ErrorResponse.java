package io.github.tooba.device_inventory_service.controller.responseDto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(description = "Standard error response returned when a request fails")
public record ErrorResponse(

        @Schema(
                description = "Application-specific error code",
                example = "DEVICE_IN_USE"
        )
        String code,

        @Schema(
                description = "Human-readable error message",
                example = "Device cannot be deleted because it is currently in use"
        )
        String message,

        @Schema(
                description = "HTTP status code",
                example = "409"
        )
        int status,

        @Schema(
                description = "Request path where the error occurred",
                example = "/devices/550e8400-e29b-41d4-a716-446655440000"
        )
        String path,

        @Schema(
                description = "Timestamp of when the error occurred (UTC)",
                example = "2025-06-01T10:15:30Z"
        )
        Instant timestamp,

        @Schema(
                description = "Field-level validation errors (only present for validation failures)",
                example = """
                        {
                          "name": "Name must not be blank",
                          "state": "State must not be null"
                        }
                        """
        )
        Map<String, String> validationErrors
) {}