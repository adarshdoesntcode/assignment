package com.payment.repository.projection;

import io.micronaut.core.annotation.Introspected;

import java.math.BigDecimal;

@Introspected
public class AmountStatsProjection {
    private BigDecimal avgAmount;
    private BigDecimal medianAmount;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    public AmountStatsProjection() {
    }

    public AmountStatsProjection(BigDecimal avgAmount, BigDecimal medianAmount, BigDecimal minAmount,
            BigDecimal maxAmount) {
        this.avgAmount = avgAmount;
        this.medianAmount = medianAmount;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public BigDecimal getAvgAmount() {
        return avgAmount;
    }

    public void setAvgAmount(BigDecimal avgAmount) {
        this.avgAmount = avgAmount;
    }

    public BigDecimal getMedianAmount() {
        return medianAmount;
    }

    public void setMedianAmount(BigDecimal medianAmount) {
        this.medianAmount = medianAmount;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }
}
