package com.softavail.vehicle.service.impl;

import com.softavail.vehicle.dto.MaintenanceFrequencyResponse;
import com.softavail.vehicle.dto.MaintenanceScore;
import com.softavail.vehicle.exception.MaintenanceFrequencyNotFoundException;
import io.micronaut.http.HttpStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaintenanceScoreMapping {
    private static final Map<String, MaintenanceScore> MAINTENANCE_FREQUENCY_MAPPINGS =
            Map.of(
                    "very_low", MaintenanceScore.POOR,
                    "low", MaintenanceScore.POOR,
                    "medium", MaintenanceScore.AVERAGE,
                    "high", MaintenanceScore.GOOD
            );

    public static Mono<MaintenanceScore> getMaintenanceScore(MaintenanceFrequencyResponse maintenanceFrequencyResponse) {
        String maintenanceFrequency = maintenanceFrequencyResponse.maintenanceFrequency();
        if (!MAINTENANCE_FREQUENCY_MAPPINGS.containsKey(maintenanceFrequency)) {
            return Mono.error(() -> new MaintenanceFrequencyNotFoundException(HttpStatus.NOT_FOUND,
                    "Cannot find given MaintenanceFrequency")
            );
        }
        return Mono.fromSupplier(() -> MAINTENANCE_FREQUENCY_MAPPINGS.get(maintenanceFrequency));
    }
}