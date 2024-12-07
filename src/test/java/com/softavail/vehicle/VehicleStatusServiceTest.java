package com.softavail.vehicle;

import io.micronaut.core.io.ResourceLoader;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
class VehicleStatusServiceTest {

    @Inject
    EmbeddedApplication<?> application;

    @Test
    void testItWorks(ResourceLoader resourceLoader) {
        Assertions.assertTrue(application.isRunning());
        Assertions.assertTrue(resourceLoader.getResource("META-INF/swagger/vehicle-status-service-1.0.yml")
                .isPresent());
    }
}
