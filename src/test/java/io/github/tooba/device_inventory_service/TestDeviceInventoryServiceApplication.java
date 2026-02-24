package io.github.tooba.device_inventory_service;

import org.springframework.boot.SpringApplication;

public class TestDeviceInventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(DeviceInventoryServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
