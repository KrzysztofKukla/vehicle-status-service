package com.softavail.vehicle.service.impl;

import com.softavail.vehicle.client.MaintenanceClient;
import com.softavail.vehicle.dto.*;
import com.softavail.vehicle.exception.MaintenanceNotFoundException;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CarMaintenanceServiceTest {

    RequestId requestId = new RequestId("1234");
    Vin vin = new Vin("4Y1SL65848Z411439");
    Integer retryMaxAttempt = 3;
    Integer retryDelay = 10;

    @Mock
    MaintenanceClient maintenanceClient;

    CarMaintenanceService carMaintenanceService;

    @BeforeEach
    void setUp() {
        carMaintenanceService = new CarMaintenanceService(maintenanceClient, retryMaxAttempt, retryDelay);
    }

    @Test
    void shouldReturnUnknownMaintenanceScoreWhenMaintenanceIsNotGivenInRequest() {
        carMaintenanceService.getMaintenance(requestId, vin, List.of(FeatureType.ACCIDENT_FREE))
                .as(content -> StepVerifier.withVirtualTime(() -> (content)))
                .expectNext(MaintenanceScore.UNKNOWN)
                .then(() -> BDDMockito.then(maintenanceClient).shouldHaveNoInteractions())
                .verifyComplete();
    }

    @Test
    void shouldReturnAverageMaintenanceScoreForMediumFrequency() {
        MaintenanceFrequencyResponse maintenanceFrequencyResponse = new MaintenanceFrequencyResponse("medium");
        List<FeatureType> featureTypes = List.of(FeatureType.MAINTENANCE);
        BDDMockito.given(maintenanceClient.getMaintenanceFrequencyByVin(vin.value()))
                .willReturn(Mono.just(maintenanceFrequencyResponse));

        carMaintenanceService.getMaintenance(requestId, vin, featureTypes)
                .as(content -> StepVerifier.create(content))
                .then(() -> BDDMockito.then(maintenanceClient).should().getMaintenanceFrequencyByVin(vin.value()))
                .expectNext(MaintenanceScore.AVERAGE)
                .verifyComplete();
    }

    @Test
    void shouldThrowMaintenanceNotFoundExceptionWhenVinDoesNotExist() {
        List<FeatureType> featureTypes = List.of(FeatureType.MAINTENANCE);
        BDDMockito.given(maintenanceClient.getMaintenanceFrequencyByVin(vin.value()))
                .willReturn(Mono.empty());

        carMaintenanceService.getMaintenance(requestId, vin, featureTypes)
                .as(content -> StepVerifier.create(content))
                .then(() -> BDDMockito.then(maintenanceClient).should().getMaintenanceFrequencyByVin(vin.value()))
                .expectError(MaintenanceNotFoundException.class)
                .verify();
    }

    @Test
    void shouldReturnClientResponseExceptionWhenServiceUnavailable() {
        List<FeatureType> featureTypes = List.of(FeatureType.MAINTENANCE);
        BDDMockito.given(maintenanceClient.getMaintenanceFrequencyByVin(vin.value()))
                .willReturn(Mono.error(new HttpClientResponseException("Service unavailable", HttpResponse.badRequest())));

        carMaintenanceService.getMaintenance(requestId, vin, featureTypes)
                .as(result -> StepVerifier.create(result))
                .then(() -> BDDMockito.then(maintenanceClient).should().getMaintenanceFrequencyByVin(vin.value()))
                .expectError(HttpClientResponseException.class)
                .verify(Duration.ofSeconds(10));
    }

    @Test
    void shouldReturnInsuranceNotFoundExceptionWhenOtherExceptionsThrown() {
        List<FeatureType> featureTypes = List.of(FeatureType.MAINTENANCE);
        BDDMockito.given(maintenanceClient.getMaintenanceFrequencyByVin(vin.value()))
                .willReturn(Mono.error(new RuntimeException("Something went wrong")));

        carMaintenanceService.getMaintenance(requestId, vin, featureTypes)
                .as(result -> StepVerifier.create(result))
                .then(() -> BDDMockito.then(maintenanceClient).should().getMaintenanceFrequencyByVin(vin.value()))
                .expectError(MaintenanceNotFoundException.class)
                .verify();
    }
}