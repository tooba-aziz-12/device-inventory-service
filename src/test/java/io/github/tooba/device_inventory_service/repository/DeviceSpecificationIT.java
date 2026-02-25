package io.github.tooba.device_inventory_service.repository;

import io.github.tooba.device_inventory_service.constant.DeviceState;
import io.github.tooba.device_inventory_service.entity.Device;
import io.github.tooba.device_inventory_service.repository.specification.DeviceSpecifications;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DeviceSpecificationIT {

    @Autowired
    private DeviceRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("filters by brand only (case-insensitive)")
    void filtersByBrandOnly() {

        repository.save(Device.create("iPhone", "Apple", DeviceState.AVAILABLE));
        repository.save(Device.create("Galaxy", "Samsung", DeviceState.AVAILABLE));

        Specification<Device> spec =
                Specification.where(DeviceSpecifications.hasBrand("apple"))
                             .and(DeviceSpecifications.hasState(null));

        Page<Device> result =
                repository.findAll(spec, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getBrand()).isEqualTo("Apple");
    }

    @Test
    @DisplayName("filters by state only")
    void filtersByStateOnly() {

        repository.save(Device.create("iPhone", "Apple", DeviceState.AVAILABLE));
        repository.save(Device.create("Galaxy", "Samsung", DeviceState.IN_USE));

        Specification<Device> spec =
                Specification.where(DeviceSpecifications.hasBrand(null))
                             .and(DeviceSpecifications.hasState(DeviceState.IN_USE));

        Page<Device> result =
                repository.findAll(spec, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getState())
                .isEqualTo(DeviceState.IN_USE);
    }

    @Test
    @DisplayName("filters by brand and state together")
    void filtersByBrandAndState() {

        repository.save(Device.create("iPhone", "Apple", DeviceState.AVAILABLE));
        repository.save(Device.create("iPhone 15", "Apple", DeviceState.IN_USE));
        repository.save(Device.create("Galaxy", "Samsung", DeviceState.AVAILABLE));

        Specification<Device> spec =
                Specification.where(DeviceSpecifications.hasBrand("Apple"))
                             .and(DeviceSpecifications.hasState(DeviceState.IN_USE));

        Page<Device> result =
                repository.findAll(spec, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName())
                .isEqualTo("iPhone 15");
    }
}