package io.github.tooba.device_inventory_service.service;

import io.github.tooba.device_inventory_service.constant.DeviceState;
import io.github.tooba.device_inventory_service.entity.Device;
import io.github.tooba.device_inventory_service.repository.DeviceRepository;
import io.github.tooba.device_inventory_service.service.command.CreateDeviceCommand;
import io.github.tooba.device_inventory_service.service.command.PatchDeviceCommand;
import io.github.tooba.device_inventory_service.service.command.UpdateDeviceCommand;
import io.github.tooba.device_inventory_service.service.exception.DeviceNotFoundException;
import io.github.tooba.device_inventory_service.service.result.DeviceResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.tooba.device_inventory_service.repository.specification.DeviceSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

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

    @Transactional
    public DeviceResult update(UpdateDeviceCommand command) {

        Device device = repo.findById(command.id())
                .orElseThrow(() ->
                        new DeviceNotFoundException(
                                "Device not found with id: " + command.id()
                        )
                );

        device.update(
                command.normalizedName(),
                command.normalizedBrand(),
                command.state()
        );

        Device saved = repo.save(device);

        return new DeviceResult(
                saved.getId(),
                saved.getName(),
                saved.getBrand(),
                saved.getState(),
                saved.getCreationTime()
        );
    }
    @Transactional(readOnly = true)
    public DeviceResult getById(UUID id) {

        Device device = repo.findById(id)
                .orElseThrow(() ->
                        new DeviceNotFoundException(
                                "Device not found with id: " + id
                        )
                );

        return new DeviceResult(
                device.getId(),
                device.getName(),
                device.getBrand(),
                device.getState(),
                device.getCreationTime()
        );
    }

    @Transactional(readOnly = true)
    public Page<DeviceResult> getAll(
            String brand,
            DeviceState state,
            Pageable pageable
    ) {

        Specification<Device> spec = Specification
                .where(DeviceSpecifications.hasBrand(brand))
                .and(DeviceSpecifications.hasState(state));

        Page<Device> devices = repo.findAll(spec, pageable);

        return devices.map(device ->
                new DeviceResult(
                        device.getId(),
                        device.getName(),
                        device.getBrand(),
                        device.getState(),
                        device.getCreationTime()
                )
        );
    }
    @Transactional
    public DeviceResult patch(PatchDeviceCommand command) {

        Device device = repo.findById(command.id())
                .orElseThrow(() ->
                        new DeviceNotFoundException(
                                "Device not found with id: " + command.id()
                        )
                );

        device.patch(
                command.name(),
                command.brand(),
                command.state()
        );

        Device saved = repo.save(device);

        return new DeviceResult(
                saved.getId(),
                saved.getName(),
                saved.getBrand(),
                saved.getState(),
                saved.getCreationTime()
        );
    }
}