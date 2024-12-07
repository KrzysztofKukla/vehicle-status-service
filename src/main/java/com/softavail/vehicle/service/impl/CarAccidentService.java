package com.softavail.vehicle.service.impl;

import com.softavail.vehicle.client.InsuranceClient;
import com.softavail.vehicle.dto.FeatureType;
import com.softavail.vehicle.dto.InsuranceResponse;
import com.softavail.vehicle.dto.RequestId;
import com.softavail.vehicle.dto.Vin;
import com.softavail.vehicle.exception.InsuranceNotFoundException;
import com.softavail.vehicle.service.CarAccident;
import com.softavail.vehicle.utils.RetryHandler;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CarAccidentService implements CarAccident {
    private static final String INSURANCE_EXCEPTION_MESSAGE = "Cannot receive Insurance for given vin: ";

    private final InsuranceClient insuranceClient;
    private final Integer retryMaxAttempt;
    private final Integer retryDelay;

    @Override
    public Mono<Boolean> getInsurance(RequestId requestId, Vin vin, List<FeatureType> featureTypes) {
        if (!featureTypes.contains(FeatureType.ACCIDENT_FREE)) {
            return Mono.fromSupplier(() -> Boolean.FALSE);
        }
        return insuranceClient.checkCarByVin(vin.value())
                .doFirst(() -> log.info("{} request calls to external Insurance API service", requestId.value()))
                .switchIfEmpty(
                        Mono.error(
                                new InsuranceNotFoundException(HttpStatus.NOT_FOUND, INSURANCE_EXCEPTION_MESSAGE + vin.value())
                        )
                )
                .doOnError(HttpClientResponseException.class, httpClientResponseException -> {
                    log.error("{} Insurance service unavailable ", httpClientResponseException.getServiceId());
                })
                .onErrorMap(throwable -> !(throwable instanceof HttpClientResponseException),
                        throwable -> new InsuranceNotFoundException(HttpStatus.NOT_FOUND,
                                INSURANCE_EXCEPTION_MESSAGE + vin.value())
                )
                .flatMap(insuranceResponse -> validateClaims(vin, insuranceResponse))
                .retryWhen(RetryHandler.makeRetry("Insurance", retryMaxAttempt, retryDelay));
    }

    private Mono<Boolean> validateClaims(Vin vin, InsuranceResponse insuranceResponse) {
        if (insuranceResponse == null
                || insuranceResponse.report() == null
                || insuranceResponse.report().claims() == null)
            return Mono.error(new InsuranceNotFoundException(HttpStatus.NOT_FOUND,
                    INSURANCE_EXCEPTION_MESSAGE + vin.value()));
        return Mono.fromSupplier(() -> insuranceResponse.report().claims() > 0);
    }
}
