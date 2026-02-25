package io.github.tooba.device_inventory_service.service;

import io.github.tooba.device_inventory_service.constant.DeviceState;
import io.github.tooba.device_inventory_service.entity.Device;
import io.github.tooba.device_inventory_service.fixture.DeviceTestDataFactory;
import io.github.tooba.device_inventory_service.repository.DeviceRepository;
import io.github.tooba.device_inventory_service.service.command.CreateDeviceCommand;
import io.github.tooba.device_inventory_service.service.command.UpdateDeviceCommand;
import io.github.tooba.device_inventory_service.service.exception.DeviceNotFoundException;
import io.github.tooba.device_inventory_service.service.result.DeviceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.Optional;
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

    @Nested
    @DisplayName("create()")
    class CreateDeviceServiceTests {

        @Test
        @DisplayName("should persist device and return result")
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
        @DisplayName("should propagate repository exception")
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

    @Nested
    @DisplayName("update()")
    class UpdateDeviceServiceTests {
        @Test
        @DisplayName("should update device successfully")
        void shouldUpdateDevice() {

            UUID id = UUID.randomUUID();
            Instant originalCreationTime = Instant.now();

            Device existing = DeviceTestDataFactory.builder()
                    .withId(id)
                    .withName("iPhone")
                    .withBrand("Apple")
                    .withState(DeviceState.AVAILABLE)
                    .withCreationTime(originalCreationTime)
                    .build();

            Device expectedUpdated = DeviceTestDataFactory.builder()
                    .withId(id)
                    .withName("Galaxy")
                    .withBrand("Samsung")
                    .withState(DeviceState.IN_USE)
                    .withCreationTime(originalCreationTime)
                    .build();

            when(repository.findById(id)).thenReturn(Optional.of(existing));
            when(repository.save(any(Device.class))).thenReturn(expectedUpdated);

            UpdateDeviceCommand command =
                    new UpdateDeviceCommand(id, "Galaxy", "Samsung", DeviceState.IN_USE);

            DeviceResult result = service.update(command);

            assertThat(result.name()).isEqualTo("Galaxy");
            assertThat(result.brand()).isEqualTo("Samsung");
            assertThat(result.state()).isEqualTo(DeviceState.IN_USE);
            assertThat(result.creationTime()).isEqualTo(originalCreationTime);

            verify(repository).save(any(Device.class));
        }
        @Test
        @DisplayName("should throw DeviceNotFoundException when device does not exist")
        void shouldThrowWhenNotFound() {

            UUID id = UUID.randomUUID();

            when(repository.findById(id)).thenReturn(Optional.empty());

            UpdateDeviceCommand command =
                    new UpdateDeviceCommand(id, "Galaxy", "Samsung", DeviceState.AVAILABLE);

            assertThatThrownBy(() -> service.update(command))
                    .isInstanceOf(DeviceNotFoundException.class);

            verify(repository, never()).save(any());
        }
        @Test
        @DisplayName("should not allow name or brand update when device is IN_USE")
        void shouldBlockNameAndBrandUpdateWhenInUse() {

            UUID id = UUID.randomUUID();

            Device existing = DeviceTestDataFactory.builder()
                    .withId(id)
                    .withName("iPhone")
                    .withBrand("Apple")
                    .withState(DeviceState.IN_USE)
                    .build();

            when(repository.findById(id)).thenReturn(Optional.of(existing));

            UpdateDeviceCommand command =
                    new UpdateDeviceCommand(id, "Galaxy", "Samsung", DeviceState.IN_USE);

            assertThatThrownBy(() -> service.update(command))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("cannot be updated");

            verify(repository, never()).save(any());
        }
        @Test
        @DisplayName("should not modify creationTime during update")
        void shouldNotChangeCreationTime() {

            UUID id = UUID.randomUUID();
            Instant originalCreationTime = Instant.now();

            Device existing = DeviceTestDataFactory.builder()
                    .withId(id)
                    .withCreationTime(originalCreationTime)
                    .build();

            when(repository.findById(id)).thenReturn(Optional.of(existing));
            when(repository.save(existing)).thenReturn(existing);

            UpdateDeviceCommand command =
                    new UpdateDeviceCommand(id, "Galaxy", "Samsung", DeviceState.AVAILABLE);

            service.update(command);

            assertThat(existing.getCreationTime()).isEqualTo(originalCreationTime);
        }
    }
    @Nested
    @DisplayName("getById()")
    class GetByIdServiceTests {
        @Test
        @DisplayName("should return device when found")
        void shouldReturnDeviceWhenFound() {

            UUID id = UUID.randomUUID();

            Device existing = DeviceTestDataFactory.builder()
                    .withId(id)
                    .withName("iPhone")
                    .withBrand("Apple")
                    .withState(DeviceState.AVAILABLE)
                    .build();

            when(repository.findById(id)).thenReturn(Optional.of(existing));

            DeviceResult result = service.getById(id);

            assertThat(result.id()).isEqualTo(id);
            assertThat(result.name()).isEqualTo("iPhone");
            assertThat(result.brand()).isEqualTo("Apple");
            assertThat(result.state()).isEqualTo(DeviceState.AVAILABLE);

            verify(repository, never()).save(any());
        }
        @Test
        @DisplayName("should throw when device not found")
        void shouldThrowWhenNotFound() {

            UUID id = UUID.randomUUID();

            when(repository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getById(id))
                    .isInstanceOf(DeviceNotFoundException.class);

            verify(repository, never()).save(any());
        }
    }
}