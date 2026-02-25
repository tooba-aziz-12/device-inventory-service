package io.github.tooba.device_inventory_service.controller;

import io.github.tooba.device_inventory_service.constant.DeviceState;
import io.github.tooba.device_inventory_service.controller.requestDto.CreateDeviceRequest;
import io.github.tooba.device_inventory_service.controller.requestDto.UpdateDeviceRequest;
import io.github.tooba.device_inventory_service.controller.responseDto.DeviceResponse;
import io.github.tooba.device_inventory_service.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceControllerIT {

    @LocalServerPort
    int port;

    private RestClient client;

    @Autowired
    DeviceRepository deviceRepository;

    @BeforeEach
    void setUp() {
        deviceRepository.deleteAll();
        this.client = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }


    private CreateDeviceRequest createRequest(
            String name,
            String brand,
            DeviceState state
    ) {
        return new CreateDeviceRequest(name, brand, state);
    }

    private UpdateDeviceRequest updateRequest(
            String name,
            String brand,
            DeviceState state
    ) {
        return new UpdateDeviceRequest(name, brand, state);
    }

    @Test
    @DisplayName("POST /devices → creates device successfully")
    void createsDevice() {

        CreateDeviceRequest request =
                createRequest("iPhone 15", "Apple", DeviceState.AVAILABLE);

        DeviceResponse response = client.post()
                .uri("/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(DeviceResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.name()).isEqualTo("iPhone 15");
        assertThat(response.brand()).isEqualTo("Apple");
        assertThat(response.state()).isEqualTo(DeviceState.AVAILABLE);
        assertThat(response.creationTime()).isNotNull();
    }

    @Test
    @DisplayName("PUT /devices/{id} → updates device successfully")
    void shouldUpdateDeviceSuccessfully() {

        DeviceResponse created = client.post()
                .uri("/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest("iPhone", "Apple", DeviceState.AVAILABLE))
                .retrieve()
                .body(DeviceResponse.class);

        assertThat(created).isNotNull();

        DeviceResponse updated = client.put()
                .uri("/devices/{id}", created.id())
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRequest("Galaxy", "Samsung", DeviceState.IN_USE))
                .retrieve()
                .body(DeviceResponse.class);

        assertThat(updated).isNotNull();
        assertThat(updated.name()).isEqualTo("Galaxy");
        assertThat(updated.brand()).isEqualTo("Samsung");
        assertThat(updated.state()).isEqualTo(DeviceState.IN_USE);

        assertThat(updated.creationTime()).isEqualTo(created.creationTime());
    }
    @Test
    @DisplayName("GET /devices/{id} → returns device successfully")
    void shouldFetchDeviceById() {

        DeviceResponse created = client.post()
                .uri("/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest("iPhone", "Apple", DeviceState.AVAILABLE))
                .retrieve()
                .body(DeviceResponse.class);

        DeviceResponse fetched = client.get()
                .uri("/devices/{id}", created.id())
                .retrieve()
                .body(DeviceResponse.class);

        assertThat(fetched).isNotNull();
        assertThat(fetched.id()).isEqualTo(created.id());
        assertThat(fetched.name()).isEqualTo("iPhone");
        assertThat(fetched.brand()).isEqualTo("Apple");
        assertThat(fetched.state()).isEqualTo(DeviceState.AVAILABLE);
        assertThat(fetched.creationTime()).isEqualTo(created.creationTime());
    }
    @Test
    void shouldFetchDevicesWithPaginationAndFiltering() throws Exception {

        client.post()
                .uri("/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest("iPhone", "Apple", DeviceState.AVAILABLE))
                .retrieve()
                .body(DeviceResponse.class);

        client.post()
                .uri("/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest("Galaxy", "Samsung", DeviceState.AVAILABLE))
                .retrieve()
                .body(DeviceResponse.class);

        String response = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/devices")
                        .queryParam("brand", "Apple")
                        .queryParam("page", 0)
                        .queryParam("size", 1)
                        .build())
                .retrieve()
                .body(String.class);

        var json = new ObjectMapper().readTree(response);

        // Pagination metadata
        assertThat(json.get("number").asInt()).isEqualTo(0);
        assertThat(json.get("totalElements").asInt()).isEqualTo(1);
        assertThat(json.get("totalPages").asInt()).isEqualTo(1);

        // Content
        var content = json.get("content");
        assertThat(content).isNotNull();
        assertThat(content.size()).isEqualTo(1);

        var first = content.get(0);
        assertThat(first.get("name").asText()).isEqualTo("iPhone");
        assertThat(first.get("brand").asText()).isEqualTo("Apple");
    }
    @Test
    void shouldPartiallyUpdateDevice() {

        DeviceResponse created = client.post()
                .uri("/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .body(createRequest("iPhone", "Apple", DeviceState.AVAILABLE))
                .retrieve()
                .body(DeviceResponse.class);

        DeviceResponse patched = client.patch()
                .uri("/devices/{id}", created.id())
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                  {
                    "name": "Galaxy"
                  }
                  """)
                .retrieve()
                .body(DeviceResponse.class);

        assertThat(patched.name()).isEqualTo("Galaxy");
        assertThat(patched.brand()).isEqualTo("Apple");
        assertThat(patched.creationTime())
                .isEqualTo(created.creationTime());
    }
}