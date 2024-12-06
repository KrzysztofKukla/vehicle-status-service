package com.softavail.vehicle.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record Report(
        Integer claims
) {
}
