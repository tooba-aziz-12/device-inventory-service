package io.github.tooba.device_inventory_service.repository.specification;

import io.github.tooba.device_inventory_service.constant.DeviceState;
import io.github.tooba.device_inventory_service.entity.Device;
import org.springframework.data.jpa.domain.Specification;

public class DeviceSpecifications {

    public static Specification<Device> hasBrand(String brand) {
        return (root, query, cb) ->
                brand == null ? null :
                        cb.equal(cb.lower(root.get("brand")), brand.toLowerCase());
    }

    public static Specification<Device> hasState(DeviceState state) {
        return (root, query, cb) ->
                state == null ? null :
                        cb.equal(root.get("state"), state);
    }
}