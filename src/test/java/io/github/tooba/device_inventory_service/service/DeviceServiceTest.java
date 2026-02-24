package io.github.tooba.device_inventory_service.service;

import io.github.tooba.device_inventory_service.constant.DeviceState;
import io.github.tooba.device_inventory_service.entity.Device;
import io.github.tooba.device_inventory_service.fixture.DeviceTestDataFactory;
import io.github.tooba.device_inventory_service.repository.DeviceRepository;
import io.github.tooba.device_inventory_service.service.command.CreateDeviceCommand;
import io.github.tooba.device_inventory_service.service.result.DeviceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeviceServiceTest {

    @Mock
    private DeviceRepository repository;

    @InjectMocks
    private DeviceService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("create() should persist device and return result")
    void shouldCreateDevice() {

        UUID id = UUID.randomUUID();

        Device persisted = DeviceTestDataFactory.builder()
                .withId(id)
                .withName("iPhone")
                .withBrand("Apple")
                .withState(DeviceState.AVAILABLE)
                .build();

        when(repository.save(any(Device.class))).thenReturn(persisted);

        CreateDeviceCommand command =
                new CreateDeviceCommand("iPhone", "Apple", DeviceState.AVAILABLE);

        DeviceResult result = service.create(command);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.name()).isEqualTo("iPhone");
        assertThat(result.brand()).isEqualTo("Apple");
        assertThat(result.state()).isEqualTo(DeviceState.AVAILABLE);

        verify(repository).save(any(Device.class));
    }


    @Test
    @DisplayName("create() should propagate repository exception")
    void shouldPropagateRepositoryException() {

        when(repository.save(any(Device.class)))
                .thenThrow(new RuntimeException("DB failure"));

        CreateDeviceCommand command =
                new CreateDeviceCommand("iPhone", "Apple", DeviceState.AVAILABLE);

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB failure");

        verify(repository).save(any(Device.class));
    }
}