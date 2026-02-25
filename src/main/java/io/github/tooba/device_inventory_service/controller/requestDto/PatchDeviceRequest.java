package io.github.tooba.device_inventory_service.controller.requestDto;

import io.github.tooba.device_inventory_service.constant.DeviceState;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Request payload for partially updating a device. " +
                "Only provided fields will be updated. " +
                "Fields left null will remain unchanged."
)
public record PatchDeviceRequest(

        @Schema(
                description = "Updated device name",
                example = "Galaxy S24",
                nullable = true
        )
        String name,

        @Schema(
                description = "Updated device brand",
                example = "Samsung",
                nullable = true
        )
        String brand,

        @Schema(
                description = "Updated device state",
                example = "IN_USE",
                allowableValues = {"AVAILABLE", "IN_USE", "INACTIVE"},
                nullable = true
        )
        DeviceState state
) {}