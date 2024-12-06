package com.softavail.vehicle.client;

import com.softavail.vehicle.dto.InsuranceResponse;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

@Client(id = "insurance", value = "${insurance.base-url}")
@Header(name = HttpHeaders.USER_AGENT, value = "insurance HTTP Client")
@Header(name = HttpHeaders.ACCEPT, value = "application/json")
public interface InsuranceClient {

    @Get("/accidents/report")
    @SingleResult
    Mono<InsuranceResponse> checkCarByVin(@QueryValue("vin") String vin);
}
