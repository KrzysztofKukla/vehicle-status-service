package com.softavail.vehicle.exception.handler;

import com.softavail.vehicle.dto.InvalidCarStatusResponse;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Produces
@Singleton
@Requires(classes = {HttpClientResponseException.class, ExceptionHandler.class})
public class ServiceNotavailableExceptionHandler implements ExceptionHandler<HttpClientResponseException,
        HttpResponse<InvalidCarStatusResponse>> {
    private static final String DEFAULT_MESSAGE = "Service temporarily unavailable, please try later";

    @Override
    public HttpResponse<InvalidCarStatusResponse> handle(HttpRequest request, HttpClientResponseException exception) {
        String path = request.getUri().getPath();
        InvalidCarStatusResponse invalidCarStatusResponse =
                new InvalidCarStatusResponse(path, DEFAULT_MESSAGE);
        return HttpResponse.status(HttpStatus.NO_RESPONSE)
                .body(invalidCarStatusResponse);
    }
}
