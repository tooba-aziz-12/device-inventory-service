package io.github.tooba.device_inventory_service.controller;


import io.github.tooba.device_inventory_service.constant.DeviceState;
import io.github.tooba.device_inventory_service.controller.requestDto.CreateDeviceRequest;
import io.github.tooba.device_inventory_service.controller.requestDto.PatchDeviceRequest;
import io.github.tooba.device_inventory_service.controller.requestDto.UpdateDeviceRequest;
import io.github.tooba.device_inventory_service.controller.responseDto.DeviceResponse;
import io.github.tooba.device_inventory_service.service.DeviceService;
import io.github.tooba.device_inventory_service.service.command.CreateDeviceCommand;
import io.github.tooba.device_inventory_service.service.command.PatchDeviceCommand;
import io.github.tooba.device_inventory_service.service.command.UpdateDeviceCommand;
import io.github.tooba.device_inventory_service.service.result.DeviceResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Devices", description = "Device management operations")
@RestController
@RequestMapping("/devices")
public class DeviceController {

    private final DeviceService service;

    public DeviceController(DeviceService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new device",
            description = "Creates a device with the given name, brand, and state. " +
                    "The creation time is automatically generated and immutable."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Device successfully created",
                    content = @Content(schema = @Schema(implementation = DeviceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Business rule violation")
    })
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
    @Operation(
            summary = "Update a device",
            description = "Performs a full update of an existing device. " +
                    "All fields must be provided. Domain rules apply."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device successfully updated",
                    content = @Content(schema = @Schema(implementation = DeviceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Device not found"),
            @ApiResponse(responseCode = "409", description = "Business rule violation")
    })
    public DeviceResponse update(
            @Parameter(description = "Device ID", required = true)
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

    @GetMapping("/{id}")
    @Operation(
            summary = "Get device by ID",
            description = "Retrieves a single device by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device found",
                    content = @Content(schema = @Schema(implementation = DeviceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public DeviceResponse getById(
            @Parameter(description = "Device ID", required = true)
            @PathVariable UUID id
    ) {
        DeviceResult result = service.getById(id);
        return DeviceResponse.from(result);
    }

    @GetMapping
    @Operation(
            summary = "List devices",
            description = "Retrieves devices with optional filtering by brand and state. " +
                    "Supports pagination and sorting."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Devices retrieved successfully")
    })
    public Page<DeviceResponse> getAll(
            @Parameter(description = "Filter by brand")
            @RequestParam(required = false) String brand,

            @Parameter(description = "Filter by device state",
                    schema = @Schema(implementation = DeviceState.class))
            @RequestParam(required = false) DeviceState state,

            @Parameter(hidden = true)
            Pageable pageable
    ) {
        Page<DeviceResult> resultPage =
                service.getAll(brand, state, pageable);

        return resultPage.map(DeviceResponse::from);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Partially update a device",
            description = "Updates only provided fields of a device. " +
                    "Domain rules are enforced for state-based restrictions."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device successfully updated",
                    content = @Content(schema = @Schema(implementation = DeviceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Device not found"),
            @ApiResponse(responseCode = "409", description = "Business rule violation")
    })
    public DeviceResponse patch(
            @Parameter(description = "Device ID", required = true)
            @PathVariable UUID id,
            @RequestBody PatchDeviceRequest request
    ) {
        var command = new PatchDeviceCommand(
                id,
                request.name(),
                request.brand(),
                request.state()
        );

        DeviceResult result = service.patch(command);
        return DeviceResponse.from(result);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a device",
            description = "Deletes a device by ID. Devices in IN_USE state cannot be deleted."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Device successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Device not found"),
            @ApiResponse(responseCode = "409", description = "Device is in use and cannot be deleted")
    })
    public void delete(
            @Parameter(description = "Device ID", required = true)
            @PathVariable UUID id
    ) {
        service.delete(id);
    }
}