package com.softavail.vehicle.service.impl;

import com.softavail.vehicle.dto.*;
import com.softavail.vehicle.service.CarAccident;
import com.softavail.vehicle.service.CarMaintenance;
import com.softavail.vehicle.service.CarStatusChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CarStatusCheckerService implements CarStatusChecker {
    private final CarAccident carAccident;
    private final CarMaintenance carMaintenance;

    @Override
    public Mono<CarStatusResponse> getStatus(RequestId requestId, CarCheckerRequest carCheckerRequest) {
        Vin vin = new Vin(carCheckerRequest.vin());
        List<FeatureType> featureTypes = toFeatureEnum(carCheckerRequest.features());
        Mono<Boolean> accidentFree = carAccident.getInsurance(requestId, vin, featureTypes);
        Mono<MaintenanceScore> maintenanceScore = carMaintenance.getMaintenance(requestId, vin, featureTypes);
        return Mono.zip(accidentFree, maintenanceScore)
                .map(tuple -> new CarStatusResponse(requestId.value(), vin.value(), tuple.getT1(), tuple.getT2().name().toLowerCase()));
    }

    private List<FeatureType> toFeatureEnum(List<String> stringFeatures) {
        return stringFeatures.stream()
                .map(FeatureType::fromName)
                .toList();
    }
}
