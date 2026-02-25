package io.github.tooba.device_inventory_service.entity;

import io.github.tooba.device_inventory_service.constant.DeviceState;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceState state;

    @Column(nullable = false, updatable = false)
    private Instant creationTime;

    protected Device() {}

    public Device(String name, String brand, DeviceState state) {
        this.name = name;
        this.brand = brand;
        this.state = state;
    }

    @PrePersist
    void onCreate() {
        if (creationTime == null) creationTime = Instant.now();
        if (state == null) state = DeviceState.AVAILABLE;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getBrand() { return brand; }
    public DeviceState getState() { return state; }
    public Instant getCreationTime() { return creationTime; }

    public static Device create(String name, String brand, DeviceState state) {
        return new Device(name, brand, state);
    }

    public void update(String name, String brand, DeviceState newState) {

        if (this.state == DeviceState.IN_USE) {
            if (!this.name.equals(name) || !this.brand.equals(brand)) {
                throw new IllegalStateException(
                        "Name and brand cannot be updated while device is in use"
                );
            }
        }

        this.name = name;
        this.brand = brand;
        this.state = newState;
    }
    public void patch(String name, String brand, DeviceState newState) {

        if (this.state == DeviceState.IN_USE) {
            if (name != null && !this.name.equals(name)) {
                throw new IllegalStateException(
                        "Name cannot be updated while device is in use"
                );
            }
            if (brand != null && !this.brand.equals(brand)) {
                throw new IllegalStateException(
                        "Brand cannot be updated while device is in use"
                );
            }
        }

        if (name != null && !name.isBlank()) {
            this.name = name.trim();
        }

        if (brand != null && !brand.isBlank()) {
            this.brand = brand.trim();
        }

        if (newState != null) {
            this.state = newState;
        }
    }
}