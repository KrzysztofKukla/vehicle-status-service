package com.softavail.vehicle.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record CarStatusResponse(
        String requestId,
        String vin,
        @JsonProperty("accident_free")
        Boolean accidentFree,
        @JsonProperty("maintenance_score")
        String maintenanceScore
) {
}
