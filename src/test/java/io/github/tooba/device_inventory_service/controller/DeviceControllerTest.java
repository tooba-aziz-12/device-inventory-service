package io.github.tooba.device_inventory_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tooba.device_inventory_service.constant.DeviceState;
import io.github.tooba.device_inventory_service.controller.requestDto.CreateDeviceRequest;
import io.github.tooba.device_inventory_service.service.DeviceService;
import io.github.tooba.device_inventory_service.service.result.DeviceResult;
import org.junit.jupiter.api.DisplayName;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(DeviceController.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean
    private DeviceService service;

    @Test
    @DisplayName("POST /devices → 201 Created")
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
    @DisplayName("POST /devices → 400 Bad Request (validation error)")
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
    @DisplayName("POST /devices → 422 Unprocessable Entity (business rule)")
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
    @DisplayName("POST /devices → 500 Internal Server Error")
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