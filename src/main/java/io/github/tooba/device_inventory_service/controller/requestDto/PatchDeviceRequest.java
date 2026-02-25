package io.github.tooba.device_inventory_service.controller.requestDto;

import io.github.tooba.device_inventory_service.constant.DeviceState;

public record PatchDeviceRequest(
        String name,
        String brand,
        DeviceState state
) {}