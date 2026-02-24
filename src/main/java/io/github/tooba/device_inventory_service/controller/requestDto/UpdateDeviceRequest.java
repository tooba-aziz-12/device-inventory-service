package io.github.tooba.device_inventory_service.controller.requestDto;

import io.github.tooba.device_inventory_service.constant.DeviceState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateDeviceRequest(

        @NotBlank(message = "Name must not be blank")
        String name,

        @NotBlank(message = "Brand must not be blank")
        String brand,

        @NotNull(message = "State must not be null")
        DeviceState state
) {}