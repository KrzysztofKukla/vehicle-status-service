package com.softavail.vehicle.exception.handler;

import com.softavail.vehicle.dto.InvalidCarStatusResponse;
import com.softavail.vehicle.exception.MaintenanceNotFoundException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Produces
@Singleton
@Requires(classes = {MaintenanceNotFoundException.class, ExceptionHandler.class})
public class MaintenanceNotFoundExceptionHandler implements ExceptionHandler<MaintenanceNotFoundException,
        HttpResponse<InvalidCarStatusResponse>> {

    @Override
    public HttpResponse<InvalidCarStatusResponse> handle(HttpRequest request, MaintenanceNotFoundException exception) {
        String path = request.getUri().getPath();
        InvalidCarStatusResponse invalidCarStatusResponse =
                new InvalidCarStatusResponse(path, exception.getMessage());
        return HttpResponse.notFound()
                .body(invalidCarStatusResponse);
    }
}
