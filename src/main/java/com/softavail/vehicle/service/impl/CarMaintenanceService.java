package com.softavail.vehicle.service.impl;

import com.softavail.vehicle.client.MaintenanceClient;
import com.softavail.vehicle.dto.FeatureType;
import com.softavail.vehicle.dto.MaintenanceScore;
import com.softavail.vehicle.dto.RequestId;
import com.softavail.vehicle.dto.Vin;
import com.softavail.vehicle.exception.MaintenanceNotFoundException;
import com.softavail.vehicle.service.CarMaintenance;
import com.softavail.vehicle.utils.RetryHandler;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CarMaintenanceService implements CarMaintenance {
    private final MaintenanceClient maintenanceClient;
    private final Integer retryMaxAttempt;
    private final Integer retryDelay;

    @Override
    public Mono<MaintenanceScore> getMaintenance(RequestId requestId, Vin vin, List<FeatureType> featureTypes) {
        if (!featureTypes.contains(FeatureType.MAINTENANCE)) {
            return Mono.fromSupplier(() -> MaintenanceScore.UNKNOWN);
        }
        String maintenanceExceptionMessage = "Cannot receive Maintenance for given vin: " + vin.value();
        return maintenanceClient.getMaintenanceFrequencyByVin(vin.value())
                .doFirst(() -> log.info("{} request calls to external Maintenance API service", requestId.value()))
                .switchIfEmpty(
                        Mono.error(
                                () -> new MaintenanceNotFoundException(HttpStatus.NOT_FOUND, maintenanceExceptionMessage)
                        )
                )
                .doOnError(HttpClientResponseException.class, httpClientResponseException -> {
                    log.error("{} Maintenance service unavailable ", httpClientResponseException.getServiceId());
                })
                .doOnError(throwable -> log.error("Invalid Maintenance Service response"))
                .onErrorMap(throwable -> !(throwable instanceof HttpClientResponseException),
                        throwable -> new MaintenanceNotFoundException(HttpStatus.NOT_FOUND, maintenanceExceptionMessage))
                .flatMap(MaintenanceScoreMapping::getMaintenanceScore)
                .retryWhen(RetryHandler.makeRetry("Maintenance", retryMaxAttempt, retryDelay));
    }
}
