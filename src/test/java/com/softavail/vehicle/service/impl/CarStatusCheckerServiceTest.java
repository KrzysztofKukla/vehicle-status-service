package com.softavail.vehicle.service.impl;

import com.softavail.vehicle.common.DummyDataUtils;
import com.softavail.vehicle.dto.*;
import com.softavail.vehicle.service.CarAccident;
import com.softavail.vehicle.service.CarMaintenance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CarStatusCheckerServiceTest {

    RequestId requestId;

    @Mock
    CarAccident carAccident;

    @Mock
    CarMaintenance carMaintenance;

    @InjectMocks
    CarStatusCheckerService carStatusCheckerService;

    @BeforeEach
    void setUp() {
        requestId = new RequestId("12345");
    }

    @Test
    void shouldReturnValidCarStatusResponse() {
        Vin vin = new Vin("4Y1SL65848Z411439");
        List<FeatureType> featureTypes = List.of(FeatureType.ACCIDENT_FREE, FeatureType.MAINTENANCE);
        CarCheckerRequest dummyCarCheckerRequest =
                DummyDataUtils.createDummyCarRequest(vin, List.of("accident_free", "maintenance"));

        BDDMockito.given(carAccident.getInsurance(requestId, vin, featureTypes))
                .willReturn(Mono.just(Boolean.TRUE));
        BDDMockito.given(carMaintenance.getMaintenance(requestId, vin, featureTypes))
                .willReturn(Mono.just(MaintenanceScore.AVERAGE));

        carStatusCheckerService.getStatus(new RequestId("12345"), dummyCarCheckerRequest)
                .as(carStatusResponseMono -> StepVerifier.create(carStatusResponseMono))
                .then(() -> BDDMockito.then(carAccident).should().getInsurance(requestId, vin, featureTypes))
                .then(() -> BDDMockito.then(carMaintenance).should().getMaintenance(requestId, vin, featureTypes))
                .assertNext(carStatusResult -> Assertions.assertAll(
                                () -> Assertions.assertEquals(vin.value(), carStatusResult.vin()),
                                () -> Assertions.assertTrue(carStatusResult.accidentFree()),
                                () -> Assertions.assertEquals(MaintenanceScore.AVERAGE.name().toLowerCase(), carStatusResult.maintenanceScore())
                        )
                )
                .verifyComplete();
    }
}