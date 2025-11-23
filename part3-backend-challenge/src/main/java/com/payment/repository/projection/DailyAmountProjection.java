package com.payment.repository.projection;

import io.micronaut.core.annotation.Introspected;

import java.math.BigDecimal;

@Introspected
public class DailyAmountProjection {
    private String date;
    private BigDecimal avgAmount;

    public DailyAmountProjection() {
    }

    public DailyAmountProjection(String date, BigDecimal avgAmount) {
        this.date = date;
        this.avgAmount = avgAmount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getAvgAmount() {
        return avgAmount;
    }

    public void setAvgAmount(BigDecimal avgAmount) {
        this.avgAmount = avgAmount;
    }
}
