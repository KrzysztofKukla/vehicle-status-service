package com.softavail.vehicle.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class FeatureTypeUtilsTest {

    @Test
    void shouldReturnTrueForValidInput() {
        List<String> input = List.of("accident_free", "maintenance");

        Assertions.assertTrue(FeatureUtils.isValid(input));
    }

    @Test
    void shouldReturnTrueForValidAccident_free() {
        List<String> input = List.of("accident_free");

        Assertions.assertTrue(FeatureUtils.isValid(input));
    }

    @Test
    void shouldReturnFalseForNullOrEmptyInput() {
        List<String> emptyList = List.of();

        Assertions.assertFalse(FeatureUtils.isValid(null));
        Assertions.assertFalse(FeatureUtils.isValid(emptyList));
    }

    @Test
    void shouldReturnFalseForInvalidInput() {
        List<String> input = List.of("accident_free", "invalid");

        Assertions.assertFalse(FeatureUtils.isValid(input));
    }
}