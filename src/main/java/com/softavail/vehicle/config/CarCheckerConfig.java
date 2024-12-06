package com.softavail.vehicle.config;

import com.softavail.vehicle.client.InsuranceClient;
import com.softavail.vehicle.client.MaintenanceClient;
import com.softavail.vehicle.service.CarAccident;
import com.softavail.vehicle.service.CarMaintenance;
import com.softavail.vehicle.service.CarStatusChecker;
import com.softavail.vehicle.service.impl.CarAccidentService;
import com.softavail.vehicle.service.impl.CarMaintenanceService;
import com.softavail.vehicle.service.impl.CarStatusCheckerService;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;

@Factory
public class CarCheckerConfig {

    @Value("${insurance.retry.max-attempts}")
    private Integer insuranceRetryMaxAttempt;
    @Value("${insurance.retry.delay-in-millis}")
    private Integer insuranceRetryDelay;

    @Value("${maintenance.retry.max-attempts}")
    private Integer maintenanceRetryMaxAttempt;
    @Value("${maintenance.retry.delay-in-millis}")
    private Integer maintenanceRetryDelay;

    @Bean
    CarAccident carAccident(InsuranceClient insuranceClient) {
        return new CarAccidentService(insuranceClient, insuranceRetryMaxAttempt, insuranceRetryDelay);
    }

    @Bean
    CarMaintenance carMaintenance(MaintenanceClient maintenanceClient) {
        return new CarMaintenanceService(maintenanceClient, maintenanceRetryMaxAttempt, maintenanceRetryDelay);
    }

    @Bean
    CarStatusChecker carStatusChecker(CarAccident carAccident, CarMaintenance carMaintenance) {
        return new CarStatusCheckerService(carAccident, carMaintenance);
    }

}
