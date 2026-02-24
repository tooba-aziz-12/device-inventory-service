package io.github.tooba.device_inventory_service.controller.requestDto;

import io.github.tooba.device_inventory_service.constant.DeviceState;
import jakarta.validation.constraints.NotBlank;

public record CreateDeviceRequest(
        @NotBlank String name,
        @NotBlank String brand,
        DeviceState state
) {}