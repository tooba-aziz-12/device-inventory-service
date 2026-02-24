package io.github.tooba.device_inventory_service.controller;


import io.github.tooba.device_inventory_service.controller.requestDto.CreateDeviceRequest;
import io.github.tooba.device_inventory_service.controller.requestDto.UpdateDeviceRequest;
import io.github.tooba.device_inventory_service.controller.responseDto.DeviceResponse;
import io.github.tooba.device_inventory_service.service.DeviceService;
import io.github.tooba.device_inventory_service.service.command.CreateDeviceCommand;
import io.github.tooba.device_inventory_service.service.command.UpdateDeviceCommand;
import io.github.tooba.device_inventory_service.service.result.DeviceResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/devices")
public class DeviceController {

    private final DeviceService service;

    public DeviceController(DeviceService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeviceResponse create(@Valid @RequestBody CreateDeviceRequest request) {
        var command = new CreateDeviceCommand(
                request.name(),
                request.brand(),
                request.state()
        );
        DeviceResult result = service.create(command);

        return DeviceResponse.from(result);

    }

    @PutMapping("/{id}")
    public DeviceResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDeviceRequest request
    ) {

        var command = new UpdateDeviceCommand(
                id,
                request.name(),
                request.brand(),
                request.state()
        );

        DeviceResult result = service.update(command);

        return DeviceResponse.from(result);
    }
}