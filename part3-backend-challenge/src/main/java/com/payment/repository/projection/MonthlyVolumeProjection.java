package com.payment.repository.projection;

import io.micronaut.core.annotation.Introspected;

import java.math.BigDecimal;

@Introspected
public class MonthlyVolumeProjection {
    private String month;
    private Long count;
    private BigDecimal total;

    public MonthlyVolumeProjection() {
    }

    public MonthlyVolumeProjection(String month, Long count, BigDecimal total) {
        this.month = month;
        this.count = count;
        this.total = total;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
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
