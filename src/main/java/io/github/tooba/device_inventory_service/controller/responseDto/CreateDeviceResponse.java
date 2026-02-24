package io.github.tooba.device_inventory_service.controller.responseDto;

import io.github.tooba.device_inventory_service.constant.DeviceState;
import io.github.tooba.device_inventory_service.service.result.DeviceResult;

import java.time.Instant;
import java.util.UUID;

public record CreateDeviceResponse(
        UUID id,
        String name,
        String brand,
        DeviceState state,
        Instant creationTime
) {

    public static CreateDeviceResponse from(DeviceResult result) {
        return new CreateDeviceResponse(
                result.id(),
                result.name(),
                result.brand(),
                result.state(),
                result.creationTime()
        );
    }
}