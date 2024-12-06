package com.softavail.vehicle.utils;

import io.micronaut.context.annotation.Factory;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import jakarta.inject.Singleton;

import java.util.List;

@Factory
public class FeatureValidationFactory {

    @Singleton
    ConstraintValidator<Feature, List<String>> featureValidator() {
        return (value, annotationMetadata, context) -> FeatureUtils.isValid(value);
    }
}
