package io.github.tooba.device_inventory_service.service.command;

import io.github.tooba.device_inventory_service.constant.DeviceState;

import java.util.UUID;

public record PatchDeviceCommand(
        UUID id,
        String name,
        String brand,
        DeviceState state
) {}