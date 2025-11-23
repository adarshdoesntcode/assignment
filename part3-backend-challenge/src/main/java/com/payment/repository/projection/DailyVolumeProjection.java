package com.payment.repository.projection;

import io.micronaut.core.annotation.Introspected;

import java.math.BigDecimal;

@Introspected
public class DailyVolumeProjection {
    private String date;
    private Long count;
    private BigDecimal total;

    public DailyVolumeProjection() {
    }

    public DailyVolumeProjection(String date, Long count, BigDecimal total) {
        this.date = date;
        this.count = count;
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
