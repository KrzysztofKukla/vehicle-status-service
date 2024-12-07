package com.softavail.vehicle.service.impl;

import com.softavail.vehicle.dto.MaintenanceFrequencyResponse;
import com.softavail.vehicle.dto.MaintenanceScore;
import com.softavail.vehicle.exception.MaintenanceNotFoundException;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class MaintenanceScoreMappingTest {

    @Test
    void shouldReturnAverageForMediumFrequency() {
        MaintenanceFrequencyResponse maintenanceFrequency = new MaintenanceFrequencyResponse("medium");

        MaintenanceScoreMapping.getMaintenanceScore(maintenanceFrequency)
                .as(maintenanceScore -> StepVerifier.create(maintenanceScore))
                .expectNext(MaintenanceScore.AVERAGE)
                .verifyComplete();
    }

    @Test
    void shouldReturnGoodForHighFrequency() {
        MaintenanceFrequencyResponse maintenanceFrequency = new MaintenanceFrequencyResponse("high");

        MaintenanceScoreMapping.getMaintenanceScore(maintenanceFrequency)
                .as(maintenanceScore -> StepVerifier.create(maintenanceScore))
                .expectNext(MaintenanceScore.GOOD)
                .verifyComplete();
    }

    @Test
    void shouldReturnMonoErrorForInvalidFrequency() {
        MaintenanceFrequencyResponse invalidMaintenanceFrequency = new MaintenanceFrequencyResponse("very high");

        MaintenanceScoreMapping.getMaintenanceScore(invalidMaintenanceFrequency)
                .as(maintenanceScore -> StepVerifier.create(maintenanceScore))
                .expectError(MaintenanceNotFoundException.class)
                .verify();
    }
}