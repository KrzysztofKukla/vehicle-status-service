package com.softavail.vehicle.rest;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.softavail.vehicle.common.WiremockEndpointStubs;
import com.softavail.vehicle.dto.CarCheckerRequest;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import wiremock.com.fasterxml.jackson.core.JsonProcessingException;
import wiremock.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@WireMockTest
public class CarCheckerControllerIT {

    static final String CAR_CHECKER_API_POST = "/api/v1/car-checker";

    String vin = "4Y1SL65848Z411439";

    ObjectMapper objectMapper;

    @RegisterExtension
    public static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort())
            .build();

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldReturnAccidentTrueWhenGivenAccidentFreeWithClaims3() throws JsonProcessingException {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, getProperties())) {
            RestAssured.port = server.getPort();

            WiremockEndpointStubs.stub200StatusWithClaims3ForInsuranceService(vin);

            CarCheckerRequest carCheckerRequest = new CarCheckerRequest(vin, List.of("accident_free"));
            String carCheckerRequestJson = objectMapper.writeValueAsString(carCheckerRequest);

            RestAssured
                    .given().contentType(ContentType.JSON)
                    .body(carCheckerRequestJson)
                    .when()
                    .post(CAR_CHECKER_API_POST)
                    .then()
                    .statusCode(200)
                    .body("vin", Matchers.equalTo(vin))
                    .body("accident_free", Matchers.equalTo(Boolean.TRUE))
                    .body("maintenance_score", Matchers.equalTo("unknown"));
        }
    }

    @Test
    void shouldReturnAccidentFalseWhenGivenAccidentFreeWithClaims0() throws JsonProcessingException {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, getProperties())) {
            RestAssured.port = server.getPort();

            WiremockEndpointStubs.stub200StatusWithClaims0ForInsuranceService(vin);

            CarCheckerRequest carCheckerRequest = new CarCheckerRequest(vin, List.of("accident_free"));
            String carCheckerRequestJson = objectMapper.writeValueAsString(carCheckerRequest);

            RestAssured
                    .given().contentType(ContentType.JSON)
                    .body(carCheckerRequestJson)
                    .when()
                    .post(CAR_CHECKER_API_POST)
                    .then()
                    .statusCode(200)
                    .body("vin", Matchers.equalTo(vin))
                    .body("accident_free", Matchers.equalTo(Boolean.FALSE))
                    .body("maintenance_score", Matchers.equalTo("unknown"));
        }
    }

    @Test
    void shouldReturnMaintenancePoorWhenGivenMaintenance() throws JsonProcessingException {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, getProperties())) {
            RestAssured.port = server.getPort();

            WiremockEndpointStubs.stub200StatusWithFrequencyPoorForMaintenanceService(vin);

            CarCheckerRequest carCheckerRequest = new CarCheckerRequest(vin, List.of("maintenance"));
            String carCheckerRequestJson = objectMapper.writeValueAsString(carCheckerRequest);

            RestAssured
                    .given().contentType(ContentType.JSON)
                    .body(carCheckerRequestJson)
                    .when()
                    .post(CAR_CHECKER_API_POST)
                    .then()
                    .statusCode(200)
                    .body("vin", Matchers.equalTo(vin))
                    .body("accident_free", Matchers.equalTo(Boolean.FALSE))
                    .body("maintenance_score", Matchers.equalTo("poor"));
        }
    }

    @Test
    void shouldReturnValidResponseForGivenAccidentFreeAndMaintenance() throws JsonProcessingException {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, getProperties())) {
            RestAssured.port = server.getPort();

            WiremockEndpointStubs.stub200StatusWithClaims3ForInsuranceService(vin);
            WiremockEndpointStubs.stub200StatusWithFrequencyPoorForMaintenanceService(vin);

            CarCheckerRequest carCheckerRequest = new CarCheckerRequest(vin, List.of("accident_free", "maintenance"));
            String carCheckerRequestJson = objectMapper.writeValueAsString(carCheckerRequest);

            RestAssured
                    .given().contentType(ContentType.JSON)
                    .body(carCheckerRequestJson)
                    .when()
                    .post(CAR_CHECKER_API_POST)
                    .then()
                    .statusCode(200)
                    .body("vin", Matchers.equalTo(vin))
                    .body("accident_free", Matchers.equalTo(Boolean.TRUE))
                    .body("maintenance_score", Matchers.equalTo("poor"));
        }
    }

    @Test
    void shouldReturn400ForInvalidInput() throws JsonProcessingException {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, getProperties())) {
            RestAssured.port = server.getPort();

            CarCheckerRequest carCheckerRequest = new CarCheckerRequest(vin, List.of("invalid"));
            String carCheckerRequestJson = objectMapper.writeValueAsString(carCheckerRequest);

            RestAssured
                    .given().contentType(ContentType.JSON)
                    .body(carCheckerRequestJson)
                    .when()
                    .post(CAR_CHECKER_API_POST)
                    .then()
                    .statusCode(400);
        }
    }

    @Test
    void shouldReturn444ResponseWhenInsuranceServiceUnavailable() throws JsonProcessingException {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, getProperties())) {
            RestAssured.port = server.getPort();

            WiremockEndpointStubs.stub503ForInsuranceService(vin);
            WiremockEndpointStubs.stub200StatusWithFrequencyPoorForMaintenanceService(vin);

            CarCheckerRequest carCheckerRequest = new CarCheckerRequest(vin, List.of("accident_free", "maintenance"));
            String carCheckerRequestJson = objectMapper.writeValueAsString(carCheckerRequest);

            RestAssured
                    .given().contentType(ContentType.JSON)
                    .body(carCheckerRequestJson)
                    .when()
                    .post(CAR_CHECKER_API_POST)
                    .then()
                    .statusCode(444)
                    .body("path", Matchers.equalTo("/api/v1/car-checker"))
                    .body("message", Matchers.equalTo("Service temporarily unavailable, please try later"));
        }
    }

    @Test
    void shouldReturn444ResponseWhenMaintenanceServiceUnavailable() throws JsonProcessingException {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, getProperties())) {
            RestAssured.port = server.getPort();

            WiremockEndpointStubs.stub200StatusWithClaims3ForInsuranceService(vin);
            WiremockEndpointStubs.stub503ForMaintenanceService(vin);

            CarCheckerRequest carCheckerRequest = new CarCheckerRequest(vin, List.of("accident_free", "maintenance"));
            String carCheckerRequestJson = objectMapper.writeValueAsString(carCheckerRequest);

            RestAssured
                    .given().contentType(ContentType.JSON)
                    .body(carCheckerRequestJson)
                    .when()
                    .post(CAR_CHECKER_API_POST)
                    .then()
                    .statusCode(444)
                    .body("path", Matchers.equalTo("/api/v1/car-checker"))
                    .body("message", Matchers.equalTo("Service temporarily unavailable, please try later"));
        }
    }

    @Test
    void shouldReturn404ResponseWhenInsuranceHasEmptyBody() throws JsonProcessingException {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, getProperties())) {
            RestAssured.port = server.getPort();

            WiremockEndpointStubs.stub200WithEmptyBodyInsuranceService(vin);
            WiremockEndpointStubs.stub200StatusWithFrequencyPoorForMaintenanceService(vin);

            CarCheckerRequest carCheckerRequest = new CarCheckerRequest(vin, List.of("accident_free", "maintenance"));
            String carCheckerRequestJson = objectMapper.writeValueAsString(carCheckerRequest);

            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(carCheckerRequestJson)
                    .when()
                    .post(CAR_CHECKER_API_POST)
                    .then()
                    .statusCode(404)
                    .body("path", Matchers.equalTo("/api/v1/car-checker"))
                    .body("message", Matchers.startsWith("Cannot receive Insurance"));
        }
    }

    @Test
    void shouldReturn404ResponseWhenMaintenanceHasEmptyBody() throws JsonProcessingException {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, getProperties())) {
            RestAssured.port = server.getPort();

            WiremockEndpointStubs.stub200StatusWithClaims3ForInsuranceService(vin);
            WiremockEndpointStubs.stub200WithEmptyBodyMaintenanceService(vin);

            CarCheckerRequest carCheckerRequest = new CarCheckerRequest(vin, List.of("accident_free", "maintenance"));
            String carCheckerRequestJson = objectMapper.writeValueAsString(carCheckerRequest);

            RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .body(carCheckerRequestJson)
                    .when()
                    .post(CAR_CHECKER_API_POST)
                    .then()
                    .statusCode(404)
                    .body("path", Matchers.equalTo("/api/v1/car-checker"))
                    .body("message", Matchers.startsWith("Cannot find given MaintenanceFrequency"));
        }
    }

    public static Map<String, Object> getProperties() {
        return Map.of(
                "insurance.base-url", wireMock.baseUrl(),
                "maintenance.base-url", wireMock.baseUrl()
        );
    }
}