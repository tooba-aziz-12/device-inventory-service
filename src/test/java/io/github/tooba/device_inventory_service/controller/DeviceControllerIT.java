package io.github.tooba.device_inventory_service.controller;

import io.github.tooba.device_inventory_service.constant.DeviceState;
import io.github.tooba.device_inventory_service.controller.requestDto.CreateDeviceRequest;
import io.github.tooba.device_inventory_service.controller.responseDto.CreateDeviceResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceControllerIT {

    @LocalServerPort
    int port;

    @Test
    void createsDevice() {

        RestClient client = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        CreateDeviceRequest request = new CreateDeviceRequest(
                "iPhone 15",
                "Apple",
                DeviceState.AVAILABLE
        );

        CreateDeviceResponse response = client.post()
                .uri("/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(CreateDeviceResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.name()).isEqualTo("iPhone 15");
        assertThat(response.brand()).isEqualTo("Apple");
        assertThat(response.state()).isEqualTo(DeviceState.AVAILABLE);
        assertThat(response.creationTime()).isNotNull();
    }
}