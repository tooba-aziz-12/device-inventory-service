package io.github.tooba.device_inventory_service.service;

import io.github.tooba.device_inventory_service.entity.Device;
import io.github.tooba.device_inventory_service.repository.DeviceRepository;
import io.github.tooba.device_inventory_service.service.command.CreateDeviceCommand;
import io.github.tooba.device_inventory_service.service.result.DeviceResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceService {

    private final DeviceRepository repo;

    public DeviceService(DeviceRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public DeviceResult create(CreateDeviceCommand command) {

        var device = Device.create(
                command.normalizedName(),
                command.normalizedBrand(),
                command.state()
        );

        var saved = repo.save(device);

        return new DeviceResult(
                saved.getId(),
                saved.getName(),
                saved.getBrand(),
                saved.getState(),
                saved.getCreationTime()
        );
    }
}