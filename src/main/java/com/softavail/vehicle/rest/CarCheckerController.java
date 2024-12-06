package com.softavail.vehicle.rest;

import com.softavail.vehicle.dto.CarCheckerRequest;
import com.softavail.vehicle.dto.CarStatusResponse;
import com.softavail.vehicle.dto.RequestId;
import com.softavail.vehicle.service.CarStatusChecker;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Controller("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class CarCheckerController {
    private final CarStatusChecker carStatusChecker;

    @Post("/car-checker")
    @Produces
    Mono<HttpResponse<CarStatusResponse>> getVehicleStatus(@Valid @Body CarCheckerRequest carCheckerRequest) {
        String requestId = UUID.randomUUID().toString();
        return carStatusChecker.getStatus(new RequestId(requestId), carCheckerRequest)
                .doFirst(() -> log.info("Start processing new request: {}", requestId))
                .doOnError(throwable -> log.error("{} request failed", requestId))
                .doOnSuccess(carStatusResponse -> log.info("{} request completed", requestId))
                .map(carStatusResponse -> HttpResponse.ok(carStatusResponse));
    }
}
