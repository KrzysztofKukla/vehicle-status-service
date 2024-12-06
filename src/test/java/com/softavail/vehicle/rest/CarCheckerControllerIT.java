package com.softavail.vehicle.rest;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.softavail.vehicle.dto.CarCheckerRequest;
import io.micronaut.context.ApplicationContext;
import io.micronaut.http.MediaType;
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

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

@WireMockTest
class CarCheckerControllerIT {

    String vin = "4Y1SL65848Z411439";

    ObjectMapper objectMapper;

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort())
            .build();

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldReturnAccidentTrueForGivenAccidentFreeClaims3() throws JsonProcessingException {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, getProperties())) {
            RestAssured.port = server.getPort();

            stub200StatusClaims3ForInsuranceService();

            CarCheckerRequest carCheckerRequest = new CarCheckerRequest(vin, List.of("accident_free"));
            String carCheckerRequestJson = objectMapper.writeValueAsString(carCheckerRequest);

            RestAssured
                    .given().contentType(ContentType.JSON)
                    .body(carCheckerRequestJson)
                    .when()
                    .post("/api/v1/car-checker")
                    .then()
                    .statusCode(200)
                    .body("vin", Matchers.equalTo("4Y1SL65848Z411439"))
                    .body("accident_free", Matchers.equalTo(Boolean.TRUE))
                    .body("maintenance_score", Matchers.equalTo("unknown"));
        }
    }

    @Test
    void shouldReturnAccidentFalseResponseForGivenAccidentFreeClaims0() throws JsonProcessingException {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, getProperties())) {
            RestAssured.port = server.getPort();

            stub200StatusClaims0ForInsuranceService();

            CarCheckerRequest carCheckerRequest = new CarCheckerRequest(vin, List.of("accident_free"));
            String carCheckerRequestJson = objectMapper.writeValueAsString(carCheckerRequest);

            RestAssured
                    .given().contentType(ContentType.JSON)
                    .body(carCheckerRequestJson)
                    .when()
                    .post("/api/v1/car-checker")
                    .then()
                    .statusCode(200)
                    .body("vin", Matchers.equalTo("4Y1SL65848Z411439"))
                    .body("accident_free", Matchers.equalTo(Boolean.FALSE))
                    .body("maintenance_score", Matchers.equalTo("unknown"));
        }
    }

    @Test
    void shouldReturnValidResponseForGivenMaintenance() throws JsonProcessingException {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, getProperties())) {
            RestAssured.port = server.getPort();

            stub200StatusForMaintenanceService();

            CarCheckerRequest carCheckerRequest = new CarCheckerRequest(vin, List.of("maintenance"));
            String carCheckerRequestJson = objectMapper.writeValueAsString(carCheckerRequest);

            RestAssured
                    .given().contentType(ContentType.JSON)
                    .body(carCheckerRequestJson)
                    .when()
                    .post("/api/v1/car-checker")
                    .then()
                    .statusCode(200)
                    .body("vin", Matchers.equalTo("4Y1SL65848Z411439"))
                    .body("accident_free", Matchers.equalTo(Boolean.FALSE))
                    .body("maintenance_score", Matchers.equalTo("poor"));
        }
    }

    @Test
    void shouldReturnValidResponseForGivenAccidentFreeAndMaintenance() throws JsonProcessingException {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, getProperties())) {
            RestAssured.port = server.getPort();

            stub200StatusClaims3ForInsuranceService();
            stub200StatusForMaintenanceService();

            CarCheckerRequest carCheckerRequest = new CarCheckerRequest(vin, List.of("accident_free", "maintenance"));
            String carCheckerRequestJson = objectMapper.writeValueAsString(carCheckerRequest);

            RestAssured
                    .given().contentType(ContentType.JSON)
                    .body(carCheckerRequestJson)
                    .when()
                    .post("/api/v1/car-checker")
                    .then()
                    .statusCode(200)
                    .body("vin", Matchers.equalTo("4Y1SL65848Z411439"))
                    .body("accident_free", Matchers.equalTo(Boolean.TRUE))
                    .body("maintenance_score", Matchers.equalTo("poor"));
        }
    }

    @Test
    void shouldReturn404ForInvalidInput() throws JsonProcessingException {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, getProperties())) {
            RestAssured.port = server.getPort();

            CarCheckerRequest carCheckerRequest = new CarCheckerRequest(vin, List.of("invalid"));
            String carCheckerRequestJson = objectMapper.writeValueAsString(carCheckerRequest);

            RestAssured
                    .given().contentType(ContentType.JSON)
                    .body(carCheckerRequestJson)
                    .when()
                    .post("/api/v1/car-checker")
                    .then()
                    .statusCode(400);
        }
    }

    @Test
    void shouldReturn444ResponseWhenInsuranceServiceUnavailable() throws JsonProcessingException {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, getProperties())) {
            RestAssured.port = server.getPort();

            stub503ForInsuranceService();
            stub200StatusForMaintenanceService();

            CarCheckerRequest carCheckerRequest = new CarCheckerRequest(vin, List.of("accident_free", "maintenance"));
            String carCheckerRequestJson = objectMapper.writeValueAsString(carCheckerRequest);

            RestAssured
                    .given().contentType(ContentType.JSON)
                    .body(carCheckerRequestJson)
                    .when()
                    .post("/api/v1/car-checker")
                    .then()
                    .statusCode(444)
                    .body("path", Matchers.equalTo("/api/v1/car-checker"))
                    .body("message", Matchers.equalTo("Service temporarily unavailable, please try later"));
        }
    }

    private Map<String, Object> getProperties() {
        return Map.of(
                "insurance.base-url", wireMock.baseUrl(),
                "maintenance.base-url", wireMock.baseUrl()
        );
    }

    private void stub200StatusClaims3ForInsuranceService() {
        wireMock.stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/accidents/report"))
                        .withQueryParam("vin", equalTo(vin))
                        .willReturn(
                                WireMock.aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                                        .withBody("""
                                                {
                                                    "report": {
                                                       "claims": 3
                                                    }
                                                }
                                                """
                                        )
                                        .withStatus(200)
                        )
        );
    }

    private void stub200StatusClaims0ForInsuranceService() {
        wireMock.stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/accidents/report"))
                        .withQueryParam("vin", equalTo(vin))
                        .willReturn(
                                WireMock.aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                                        .withBody("""
                                                {
                                                    "report": {
                                                       "claims": 0
                                                    }
                                                }
                                                """
                                        )
                                        .withStatus(200)
                        )
        );
    }

    private void stub200StatusForMaintenanceService() {
        wireMock.stubFor(
                WireMock.get(WireMock.urlPathTemplate("/cars/{vin}"))
                        .withPathParam("vin", equalTo(vin))
                        .willReturn(
                                WireMock.aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                                        .withBody("""
                                                {
                                                    "maintenance_frequency": "low"
                                                }
                                                """
                                        )
                                        .withStatus(200)
                        )
        );
    }

    private void stub503ForInsuranceService() {
        wireMock.stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/accidents/report"))
                        .withQueryParam("vin", equalTo(vin))
                        .willReturn(
                                WireMock.aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                                        .withStatus(503)
                        )
        );
    }

}