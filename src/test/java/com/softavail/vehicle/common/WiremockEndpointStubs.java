package com.softavail.vehicle.common;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.micronaut.http.MediaType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.softavail.vehicle.rest.CarCheckerControllerIT.wireMock;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WiremockEndpointStubs {

    public static void stub200StatusWithClaims3ForInsuranceService(String vin) {
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

    public static void stub200StatusWithClaims0ForInsuranceService(String vin) {
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

    public static void stub200StatusWithFrequencyPoorForMaintenanceService(String vin) {
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

    public static void stub503ForInsuranceService(String vin) {
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

    public static void stub503ForMaintenanceService(String vin) {
        wireMock.stubFor(
                WireMock.get(WireMock.urlPathTemplate("/cars/{vin}"))
                        .withPathParam("vin", equalTo(vin))
                        .willReturn(
                                WireMock.aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                                        .withStatus(503)
                        )
        );
    }

    public static void stub200WithEmptyBodyInsuranceService(String vin) {
        wireMock.stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/accidents/report"))
                        .withQueryParam("vin", equalTo(vin))
                        .willReturn(
                                WireMock.aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                                        .withStatus(200)
                                        .withBody("{}")
                        )
        );
    }

    public static void stub200WithEmptyBodyMaintenanceService(String vin) {
        wireMock.stubFor(
                WireMock.get(WireMock.urlPathTemplate("/cars/{vin}"))
                        .withPathParam("vin", equalTo(vin))
                        .willReturn(
                                WireMock.aResponse()
                                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                                        .withStatus(200)
                                        .withBody("{}")
                        )
        );
    }
}
