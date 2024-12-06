package com.softavail.vehicle.exception;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;

public class InsuranceNotFoundException extends HttpStatusException {
    public InsuranceNotFoundException(HttpStatus status, String message) {
        super(status, message);
    }

    public InsuranceNotFoundException(HttpStatus status, Object body) {
        super(status, body);
    }
}
