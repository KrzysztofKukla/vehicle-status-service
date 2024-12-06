package com.softavail.vehicle.utils;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RetryHandler {
    public static RetryBackoffSpec makeRetry(String serviceName, Integer retryMaxAttempt, Integer retryDelay) {
        String retryExhaustedMessage = serviceName + " Service failed to process after max retries";
        return Retry.backoff(retryMaxAttempt, Duration.ofMillis(retryDelay))
                .filter(throwable -> throwable instanceof HttpClientResponseException)
                .doBeforeRetry(retrySignal -> log.info("Retrying request..."))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        new HttpClientResponseException(retryExhaustedMessage, HttpResponse.serverError())
                )
                .doAfterRetry(retrySignal -> log.info("Total retries: {}", retrySignal.totalRetries() + 1));
    }
}
