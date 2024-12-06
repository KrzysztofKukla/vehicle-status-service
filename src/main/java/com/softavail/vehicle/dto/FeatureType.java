package com.softavail.vehicle.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.util.stream.Stream;

@Serdeable
public enum FeatureType {
    ACCIDENT_FREE("accident_free"),
    MAINTENANCE("maintenance");

    final String name;

    FeatureType(String name) {
        this.name = name;
    }

    public static FeatureType fromName(String value) {
        return Stream.of(FeatureType.values())
                .filter(featureType -> featureType.name.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow();
    }

}
