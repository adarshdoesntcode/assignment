package com.payment.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for collecting and exposing application metrics
 */
@Singleton
public class MetricsService {

    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> counters = new ConcurrentHashMap<>();
    private final Map<String, Timer> timers = new ConcurrentHashMap<>();

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Record a transaction search operation
     */
    public void recordTransactionSearch(String merchantId, int resultCount) {
        Counter.builder("transaction.searches")
                .description("Number of transaction search operations")
                .tag("merchant", merchantId)
                .register(meterRegistry)
                .increment();

        Counter.builder("transaction.results")
                .description("Total number of transactions returned in searches")
                .tag("merchant", merchantId)
                .register(meterRegistry)
                .increment(resultCount);
    }

    /**
     * Time a transaction search operation
     */
    public Timer.Sample startTransactionSearchTimer() {
        return Timer.start(meterRegistry);
    }

    /**
     * Record the duration of a transaction search operation
     */
    public void recordTransactionSearchDuration(Timer.Sample timer, String merchantId) {
        timer.stop(Timer.builder("transaction.search.duration")
                .description("Duration of transaction search operations")
                .tag("merchant", merchantId)
                .register(meterRegistry));
    }

    /**
     * Record a transaction creation
     */
    public void recordTransactionCreation(String merchantId, String status) {
        Counter.builder("transaction.created")
                .description("Number of transactions created")
                .tags(Tag.of("merchant", merchantId), Tag.of("status", status))
                .register(meterRegistry)
                .increment();
    }

    /**
     * Record an error
     */
    public void recordError(String errorType, String operation) {
        Counter.builder("errors.total")
                .description("Total number of errors")
                .tags(Tag.of("type", errorType), Tag.of("operation", operation))
                .register(meterRegistry)
                .increment();
    }
}