package io.github.tooba.device_inventory_service.controller.requestDto;

import io.github.tooba.device_inventory_service.constant.DeviceState;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = "Request payload for fully updating a device. " +
                "All fields are required. This operation replaces the current device data."
)
public record UpdateDeviceRequest(

        @Schema(
                description = "Device name",
                example = "iPhone 15 Pro",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Name must not be blank")
        String name,

        @Schema(
                description = "Device brand",
                example = "Apple",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Brand must not be blank")
        String brand,

        @Schema(
                description = "Device state",
                example = "AVAILABLE",
                allowableValues = {"AVAILABLE", "IN_USE", "INACTIVE"},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "State must not be null")
        DeviceState state
) {}