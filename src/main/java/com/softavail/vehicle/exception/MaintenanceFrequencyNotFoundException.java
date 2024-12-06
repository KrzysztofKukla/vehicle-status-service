package com.softavail.vehicle.exception;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;

public class MaintenanceFrequencyNotFoundException extends HttpStatusException {
    public MaintenanceFrequencyNotFoundException(HttpStatus status, String message) {
        super(status, message);
    }

    public MaintenanceFrequencyNotFoundException(HttpStatus status, Object body) {
        super(status, body);
    }
}
