package com.softavail.vehicle.dto;

import com.softavail.vehicle.utils.Feature;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.List;

@Builder
@Serdeable
public record CarCheckerRequest(
        /**
         * vin pattern
         */
        @Pattern(regexp = "[A-HJ-NPR-Z0-9]{17}")
        String vin,

        @NotEmpty
        @Feature
        List<String> features
) {
}
