package com.softavail.vehicle.service.impl;

import com.softavail.vehicle.client.InsuranceClient;
import com.softavail.vehicle.common.DummyDataUtils;
import com.softavail.vehicle.dto.FeatureType;
import com.softavail.vehicle.dto.InsuranceResponse;
import com.softavail.vehicle.dto.RequestId;
import com.softavail.vehicle.dto.Vin;
import com.softavail.vehicle.exception.InsuranceNotFoundException;
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

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CarAccidentServiceTest {

    RequestId requestId = new RequestId("1234");
    Vin vin = new Vin("4Y1SL65848Z411439");
    Integer retryMaxAttempt = 3;
    Integer retryDelay = 10;

    @Mock
    InsuranceClient insuranceClient;

    CarAccidentService carAccidentService;

    @BeforeEach
    void setUp() {
        carAccidentService = new CarAccidentService(insuranceClient, retryMaxAttempt, retryDelay);
    }

    @Test
    void shouldReturnFalseAccidentFree() {
        carAccidentService.getInsurance(requestId, vin, List.of(FeatureType.MAINTENANCE))
                .as(content -> StepVerifier.create(content))
                .then(() -> BDDMockito.then(insuranceClient).shouldHaveNoInteractions())
                .expectNext(Boolean.FALSE)
                .verifyComplete();
    }

    @Test
    void shouldReturnTrueForValidCarCheckerRequest() {
        InsuranceResponse validInsuranceResponse = DummyDataUtils.createValidInsuranceResponse(3);
        BDDMockito.given(insuranceClient.checkCarByVin(vin.value()))
                .willReturn(Mono.just(validInsuranceResponse));
        List<FeatureType> featureTypes = List.of(FeatureType.ACCIDENT_FREE, FeatureType.MAINTENANCE);

        carAccidentService.getInsurance(requestId, vin, featureTypes)
                .as(result -> StepVerifier.create(result))
                .then(() -> BDDMockito.then(insuranceClient).should().checkCarByVin(vin.value()))
                .expectNext(Boolean.TRUE)
                .verifyComplete();
    }

    @Test
    void shouldReturnMonoErrorWhenGivenVinDoesNotExist() {
        List<FeatureType> featureTypes = List.of(FeatureType.ACCIDENT_FREE);
        BDDMockito.given(insuranceClient.checkCarByVin(vin.value()))
                .willReturn(Mono.empty());

        carAccidentService.getInsurance(requestId, vin, featureTypes)
                .as(result -> StepVerifier.create(result))
                .then(() -> BDDMockito.then(insuranceClient).should().checkCarByVin(vin.value()))
                .expectError(InsuranceNotFoundException.class)
                .verify();
    }

    @Test
    void shouldReturnFalseWhenClaimsLessThan1() {
        InsuranceResponse validInsuranceResponse = DummyDataUtils.createValidInsuranceResponse(0);
        List<FeatureType> featureTypes = List.of(FeatureType.ACCIDENT_FREE);
        BDDMockito.given(insuranceClient.checkCarByVin(vin.value()))
                .willReturn(Mono.just(validInsuranceResponse));

        carAccidentService.getInsurance(requestId, vin, featureTypes)
                .as(result -> StepVerifier.create(result))
                .then(() -> BDDMockito.then(insuranceClient).should().checkCarByVin(vin.value()))
                .expectNext(Boolean.FALSE)
                .verifyComplete();
    }

    @Test
    void shouldReturnHttpClientResponseExceptionWhenServiceUnavailable() {
        List<FeatureType> featureTypes = List.of(FeatureType.ACCIDENT_FREE);
        BDDMockito.given(insuranceClient.checkCarByVin(vin.value()))
                .willReturn(Mono.error(new HttpClientResponseException("Service unavailable", HttpResponse.badRequest())));

        carAccidentService.getInsurance(requestId, vin, featureTypes)
                .as(result -> StepVerifier.create(result))
                .then(() -> BDDMockito.then(insuranceClient).should().checkCarByVin(vin.value()))
                .expectError(HttpClientResponseException.class)
                .verify();
    }

    @Test
    void shouldReturnInsuranceNotFoundExceptionForOtherExceptions() {
        List<FeatureType> featureTypes = List.of(FeatureType.ACCIDENT_FREE);
        BDDMockito.given(insuranceClient.checkCarByVin(vin.value()))
                .willReturn(Mono.error(new RuntimeException("Something went wrong")));

        carAccidentService.getInsurance(requestId, vin, featureTypes)
                .as(result -> StepVerifier.create(result))
                .then(() -> BDDMockito.then(insuranceClient).should().checkCarByVin(vin.value()))
                .expectError(InsuranceNotFoundException.class)
                .verify();
    }
}