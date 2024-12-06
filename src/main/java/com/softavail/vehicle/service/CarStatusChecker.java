package com.softavail.vehicle.service;

import com.softavail.vehicle.dto.CarCheckerRequest;
import com.softavail.vehicle.dto.CarStatusResponse;
import com.softavail.vehicle.dto.RequestId;
import reactor.core.publisher.Mono;

public interface CarStatusChecker {

    Mono<CarStatusResponse> getStatus(RequestId requestId, CarCheckerRequest carCheckerRequest);
}
