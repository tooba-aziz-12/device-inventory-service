package io.github.tooba.device_inventory_service.controller.responseDto;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        String code,
        String message,
        int status,
        String path,
        Instant timestamp,
        Map<String, String> validationErrors
) {}