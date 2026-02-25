package io.github.tooba.device_inventory_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tooba.device_inventory_service.constant.DeviceState;
import io.github.tooba.device_inventory_service.controller.requestDto.CreateDeviceRequest;
import io.github.tooba.device_inventory_service.service.DeviceService;
import io.github.tooba.device_inventory_service.service.exception.DeviceNotFoundException;
import io.github.tooba.device_inventory_service.service.result.DeviceResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeviceController.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean
    private DeviceService service;

    @Nested
    @DisplayName("POST /devices")
    class CreateDeviceTests {

        @Test
        @DisplayName("→ 201 Created")
        void shouldCreateDevice() throws Exception {

            var id = UUID.randomUUID();

            Mockito.when(service.create(any()))
                    .thenReturn(new DeviceResult(
                            id,
                            "iPhone",
                            "Apple",
                            DeviceState.AVAILABLE,
                            Instant.now()
                    ));

            var request = new CreateDeviceRequest(
                    "iPhone",
                    "Apple",
                    DeviceState.AVAILABLE
            );

            mockMvc.perform(post("/devices")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.name").value("iPhone"))
                    .andExpect(jsonPath("$.brand").value("Apple"))
                    .andExpect(jsonPath("$.state").value("AVAILABLE"));
        }

        @Test
        @DisplayName("→ 400 Bad Request (validation error)")
        void shouldReturn400WhenValidationFails() throws Exception {

            var invalidJson = """
            {
              "name": "",
              "brand": "",
              "state": "AVAILABLE"
            }
            """;

            mockMvc.perform(post("/devices")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.path").value("/devices"))
                    .andExpect(jsonPath("$.validationErrors.name").exists())
                    .andExpect(jsonPath("$.validationErrors.brand").exists());
        }

        @Test
        @DisplayName("→ 422 Unprocessable Entity (business rule)")
        void shouldReturn422WhenBusinessRuleFails() throws Exception {

            Mockito.when(service.create(any()))
                    .thenThrow(new IllegalStateException("Business rule violated"));

            var request = new CreateDeviceRequest(
                    "iPhone",
                    "Apple",
                    DeviceState.AVAILABLE
            );

            mockMvc.perform(post("/devices")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"))
                    .andExpect(jsonPath("$.message").value("Business rule violated"))
                    .andExpect(jsonPath("$.status").value(422))
                    .andExpect(jsonPath("$.path").value("/devices"));
        }

        @Test
        @DisplayName("→ 500 Internal Server Error")
        void shouldReturn500WhenUnexpectedErrorOccurs() throws Exception {

            Mockito.when(service.create(any()))
                    .thenThrow(new RuntimeException("Unexpected"));

            var request = new CreateDeviceRequest(
                    "iPhone",
                    "Apple",
                    DeviceState.AVAILABLE
            );

            mockMvc.perform(post("/devices")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.path").value("/devices"));
        }
    }

    @Nested
    @DisplayName("PUT /devices/{id}")
    class UpdateDeviceTests {

        @Test
        @DisplayName("→ 200 OK")
        void shouldUpdateDevice() throws Exception {

            UUID id = UUID.randomUUID();

            Mockito.when(service.update(any()))
                    .thenReturn(new DeviceResult(
                            id,
                            "iPhone",
                            "Apple",
                            DeviceState.IN_USE,
                            Instant.now()
                    ));

            var request = """
                {
                  "name": "iPhone",
                  "brand": "Apple",
                  "state": "IN_USE"
                }
                """;

            mockMvc.perform(put("/devices/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.name").value("iPhone"))
                    .andExpect(jsonPath("$.brand").value("Apple"))
                    .andExpect(jsonPath("$.state").value("IN_USE"));
        }

        @Test
        @DisplayName("→ 400 Bad Request (validation error)")
        void shouldReturn400WhenValidationFails() throws Exception {

            UUID id = UUID.randomUUID();

            var invalidRequest = """
                {
                  "name": "",
                  "brand": "",
                  "state": null
                }
                """;

            mockMvc.perform(put("/devices/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.path").value("/devices/" + id));
        }

        @Test
        @DisplayName("→ 404 Not Found")
        void shouldReturn404WhenDeviceNotFound() throws Exception {

            UUID id = UUID.randomUUID();

            Mockito.when(service.update(any()))
                    .thenThrow(new DeviceNotFoundException("Device not found"));

            var request = """
                {
                  "name": "iPhone",
                  "brand": "Apple",
                  "state": "AVAILABLE"
                }
                """;

            mockMvc.perform(put("/devices/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("→ 422 Unprocessable Entity (business rule)")
        void shouldReturn422WhenBusinessRuleFails() throws Exception {

            UUID id = UUID.randomUUID();

            Mockito.when(service.update(any()))
                    .thenThrow(new IllegalStateException(
                            "Name and brand cannot be updated while device is in use"
                    ));

            var request = """
                {
                  "name": "Galaxy",
                  "brand": "Samsung",
                  "state": "IN_USE"
                }
                """;

            mockMvc.perform(put("/devices/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.code").value("BUSINESS_RULE_VIOLATION"))
                    .andExpect(jsonPath("$.status").value(422));
        }
        @Test
        @DisplayName("→ 500 Internal Server Error")
        void shouldReturn500WhenUnexpectedErrorOccurs() throws Exception {

            UUID id = UUID.randomUUID();

            Mockito.when(service.update(any()))
                    .thenThrow(new RuntimeException("Unexpected"));

            var request = """
            {
              "name": "iPhone",
              "brand": "Apple",
              "state": "AVAILABLE"
            }
            """;

            mockMvc.perform(put("/devices/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                    .andExpect(jsonPath("$.status").value(500));
        }
    }
    @Nested
    @DisplayName("GET /devices/{id}")
    class GetDeviceTests {
        @Test
        @DisplayName("→ 200 OK")
        void shouldReturnDevice() throws Exception {

            UUID id = UUID.randomUUID();

            Mockito.when(service.getById(id))
                    .thenReturn(new DeviceResult(
                            id,
                            "iPhone",
                            "Apple",
                            DeviceState.AVAILABLE,
                            Instant.now()
                    ));

            mockMvc.perform(get("/devices/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.name").value("iPhone"))
                    .andExpect(jsonPath("$.brand").value("Apple"))
                    .andExpect(jsonPath("$.state").value("AVAILABLE"));
        }
        @Test
        @DisplayName("→ 404 Not Found")
        void shouldReturn404WhenNotFound() throws Exception {

            UUID id = UUID.randomUUID();

            Mockito.when(service.getById(id))
                    .thenThrow(new DeviceNotFoundException("Not found"));

            mockMvc.perform(get("/devices/{id}", id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("PATCH /devices/{id}")
    class PatchDeviceTests {

        @Test
        void shouldPatchSuccessfully() throws Exception {

            UUID id = UUID.randomUUID();

            Mockito.when(service.patch(any()))
                    .thenReturn(new DeviceResult(
                            id,
                            "Galaxy",
                            "Apple",
                            DeviceState.AVAILABLE,
                            Instant.now()
                    ));

            var request = """
            {
              "name": "Galaxy"
            }
            """;

            mockMvc.perform(patch("/devices/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Galaxy"));
        }

        @Test
        void shouldReturn404() throws Exception {

            UUID id = UUID.randomUUID();

            Mockito.when(service.patch(any()))
                    .thenThrow(new DeviceNotFoundException("Not found"));

            mockMvc.perform(patch("/devices/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isNotFound());
        }
    }
}