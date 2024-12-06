package com.softavail.vehicle.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

class FeatureTypeTest {

    @Test
    void shouldReturnAccidentFeatureForValidName() {
        Assertions.assertEquals(FeatureType.ACCIDENT_FREE,
                FeatureType.fromName("accident_free"));
    }

    @Test
    void shouldReturnMaintenanceFeatureForValidName() {
        Assertions.assertEquals(FeatureType.MAINTENANCE,
                FeatureType.fromName("maintenance"));
    }

    @Test
    void shouldThrowExceptionForInvalidName() {
        Assertions.assertThrows(NoSuchElementException.class,
                () -> FeatureType.fromName("invalid"));
    }
}