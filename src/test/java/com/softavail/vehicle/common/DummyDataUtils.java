package com.softavail.vehicle.common;

import com.softavail.vehicle.dto.CarCheckerRequest;
import com.softavail.vehicle.dto.InsuranceResponse;
import com.softavail.vehicle.dto.Report;
import com.softavail.vehicle.dto.Vin;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DummyDataUtils {
    public static CarCheckerRequest createDummyCarRequest(Vin vin, List<String> features) {
        return CarCheckerRequest.builder()
                .vin(vin.value())
                .features(features)
                .build();
    }

    public static InsuranceResponse createValidInsuranceResponse(int claims) {
        Report report = new Report(claims);
        return new InsuranceResponse(report);
    }
}
