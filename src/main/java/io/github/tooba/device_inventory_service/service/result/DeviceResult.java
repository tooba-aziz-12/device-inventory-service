package io.github.tooba.device_inventory_service.service.result;


import io.github.tooba.device_inventory_service.constant.DeviceState;

import java.time.Instant;
import java.util.UUID;

public record DeviceResult(
        UUID id,
        String name,
        String brand,
        DeviceState state,
        Instant creationTime
) {}