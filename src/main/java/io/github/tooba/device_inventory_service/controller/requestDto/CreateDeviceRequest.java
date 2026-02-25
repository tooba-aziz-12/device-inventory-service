package io.github.tooba.device_inventory_service.controller.requestDto;

import io.github.tooba.device_inventory_service.constant.DeviceState;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateDeviceRequest(
        @Schema(description = "Device name", example = "iPhone 15")
        @NotBlank String name,
        @Schema(description = "Device brand", example = "Apple")
        @NotBlank String brand,
        @Schema(description = "Initial device state",
                example = "AVAILABLE",
                allowableValues = {"AVAILABLE", "IN_USE", "INACTIVE"})
        DeviceState state
) {}