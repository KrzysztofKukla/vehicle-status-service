package com.softavail.vehicle.utils;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface Feature {

    String MESSAGE = "possible values: accident_free or maintenance";

    /**
     * @return message The error message
     */
    String message() default "{" + MESSAGE + "}";
}
