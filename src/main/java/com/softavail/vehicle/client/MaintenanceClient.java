package com.softavail.vehicle.client;

import com.softavail.vehicle.dto.MaintenanceFrequencyResponse;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

@Client(id = "maintenance", value = "${maintenance.base-url}")
@Header(name = HttpHeaders.USER_AGENT, value = "maintenance HTTP Client")
@Header(name = HttpHeaders.ACCEPT, value = "application/json")
public interface MaintenanceClient {

    @Get("/cars/{vin}")
    @SingleResult
    Mono<MaintenanceFrequencyResponse> getMaintenanceFrequencyByVin(@PathVariable("vin") String vin);
}
