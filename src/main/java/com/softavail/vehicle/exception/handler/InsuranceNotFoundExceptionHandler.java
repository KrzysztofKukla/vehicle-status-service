package com.softavail.vehicle.exception.handler;

import com.softavail.vehicle.dto.InvalidCarStatusResponse;
import com.softavail.vehicle.exception.InsuranceNotFoundException;
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
@Requires(classes = {InsuranceNotFoundException.class, ExceptionHandler.class})
public class InsuranceNotFoundExceptionHandler implements ExceptionHandler<InsuranceNotFoundException, HttpResponse<InvalidCarStatusResponse>> {

    @Override
    public HttpResponse<InvalidCarStatusResponse> handle(HttpRequest request, InsuranceNotFoundException insuranceNotFoundException) {
        String path = request.getUri().getPath();
        InvalidCarStatusResponse invalidCarStatusResponse =
                new InvalidCarStatusResponse(path, insuranceNotFoundException.getMessage());
        return HttpResponse.notFound(invalidCarStatusResponse);
    }
}
