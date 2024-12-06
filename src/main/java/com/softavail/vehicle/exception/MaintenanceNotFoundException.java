package com.softavail.vehicle.exception;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;

public class MaintenanceNotFoundException extends HttpStatusException {
    public MaintenanceNotFoundException(HttpStatus status, String message) {
        super(status, message);
    }

    public MaintenanceNotFoundException(HttpStatus status, Object body) {
        super(status, body);
    }
}
