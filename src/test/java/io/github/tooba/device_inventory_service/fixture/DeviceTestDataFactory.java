package io.github.tooba.device_inventory_service.fixture;

import io.github.tooba.device_inventory_service.constant.DeviceState;
import io.github.tooba.device_inventory_service.entity.Device;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.UUID;

public final class DeviceTestDataFactory {

    private DeviceTestDataFactory() {
    }

    public static Device defaultDevice() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private UUID id = UUID.randomUUID();
        private String name = "iPhone";
        private String brand = "Apple";
        private DeviceState state = DeviceState.AVAILABLE;
        private Instant creationTime = Instant.now();

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withBrand(String brand) {
            this.brand = brand;
            return this;
        }

        public Builder withState(DeviceState state) {
            this.state = state;
            return this;
        }

        public Builder withCreationTime(Instant creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        public Device build() {
            Device device = Device.create(name, brand, state);

            setField(device, "id", id);
            setField(device, "creationTime", creationTime);

            return device;
        }

        private void setField(Object target, String fieldName, Object value) {
            try {
                Field field = target.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set field: " + fieldName, e);
            }
        }
    }
}