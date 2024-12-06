package com.softavail.vehicle.service;

import com.softavail.vehicle.dto.FeatureType;
import com.softavail.vehicle.dto.RequestId;
import com.softavail.vehicle.dto.Vin;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CarAccident {

    Mono<Boolean> getInsurance(RequestId requestId, Vin vin, List<FeatureType> featureTypes);
}
