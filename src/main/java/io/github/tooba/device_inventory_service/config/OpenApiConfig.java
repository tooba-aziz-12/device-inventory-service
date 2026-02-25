package io.github.tooba.device_inventory_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Device Inventory API",
                version = "1.0",
                description = "REST API for managing device inventory with domain-level rule enforcement."
        )
)
@Configuration
public class OpenApiConfig {
}