package com.softavail.vehicle.utils;

import com.softavail.vehicle.dto.FeatureType;
import io.micronaut.core.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeatureUtils {
    public static boolean isValid(@Nullable List<String> values) {
        if (values == null || values.isEmpty()) {
            return false;
        }

        return values.stream()
                .allMatch(value -> Stream.of(FeatureType.values())
                        .anyMatch(feature -> value.equalsIgnoreCase(feature.name()))
                );
    }
}
