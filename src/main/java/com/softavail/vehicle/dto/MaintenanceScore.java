package com.softavail.vehicle.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public enum MaintenanceScore {
    POOR,
    AVERAGE,
    GOOD,
    UNKNOWN
}
