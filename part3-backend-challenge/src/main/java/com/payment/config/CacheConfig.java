package com.payment.config;

import io.micronaut.cache.CacheManager;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.SyncCache;
import jakarta.inject.Singleton;

import java.time.Duration;
import java.util.Map;

/**
 * Cache configuration for transaction service
 */
@Singleton
@ConfigurationProperties("app.cache")
public class CacheConfig {

    private Duration transactionCacheTtl = Duration.ofMinutes(10);
    private int transactionCacheMaxSize = 1000;
    private boolean enabled = true;

    public Duration getTransactionCacheTtl() {
        return transactionCacheTtl;
    }

    public void setTransactionCacheTtl(Duration transactionCacheTtl) {
        this.transactionCacheTtl = transactionCacheTtl;
    }

    public int getTransactionCacheMaxSize() {
        return transactionCacheMaxSize;
    }

    public void setTransactionCacheMaxSize(int transactionCacheMaxSize) {
        this.transactionCacheMaxSize = transactionCacheMaxSize;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}