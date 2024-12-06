package com.softavail.vehicle.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record MaintenanceFrequencyResponse(
        @JsonProperty("maintenance_frequency")
        String maintenanceFrequency
) {
}
