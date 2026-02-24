package io.github.tooba.device_inventory_service.service.command;


import io.github.tooba.device_inventory_service.constant.DeviceState;

import java.util.Objects;

public record CreateDeviceCommand(
        String name,
        String brand,
        DeviceState state
) {

    public CreateDeviceCommand {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(brand, "brand must not be null");
        Objects.requireNonNull(state, "state must not be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }

        if (brand.isBlank()) {
            throw new IllegalArgumentException("brand must not be blank");
        }
    }

    public String normalizedName() {
        return name.trim();
    }

    public String normalizedBrand() {
        return brand.trim();
    }
}