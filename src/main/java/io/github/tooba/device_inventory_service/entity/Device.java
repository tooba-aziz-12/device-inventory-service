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
}