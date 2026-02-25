package io.github.tooba.device_inventory_service.controller.responseDto;

import io.github.tooba.device_inventory_service.constant.DeviceState;
import io.github.tooba.device_inventory_service.service.result.DeviceResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Represents a device in the inventory system")
public record DeviceResponse(

        @Schema(
                description = "Unique device identifier",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,

        @Schema(
                description = "Device name",
                example = "iPhone 15 Pro"
        )
        String name,

        @Schema(
                description = "Device brand",
                example = "Apple"
        )
        String brand,

        @Schema(
                description = "Current device state",
                example = "AVAILABLE",
                allowableValues = {"AVAILABLE", "IN_USE", "INACTIVE"}
        )
        DeviceState state,

        @Schema(
                description = "Timestamp when the device was created (UTC)",
                example = "2025-06-01T10:15:30Z"
        )
        Instant creationTime
) {

    public static DeviceResponse from(DeviceResult result) {
        return new DeviceResponse(
                result.id(),
                result.name(),
                result.brand(),
                result.state(),
                result.creationTime()
        );
    }
}