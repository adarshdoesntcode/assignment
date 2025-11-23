package com.payment.repository.projection;

import io.micronaut.core.annotation.Introspected;

import java.math.BigDecimal;

@Introspected
public class WeeklyVolumeProjection {
    private String weekStart;
    private Integer weekNumber;
    private Long count;
    private BigDecimal total;

    public WeeklyVolumeProjection() {
    }

    public WeeklyVolumeProjection(String weekStart, Integer weekNumber, Long count, BigDecimal total) {
        this.weekStart = weekStart;
        this.weekNumber = weekNumber;
        this.count = count;
        this.total = total;
    }

    public String getWeekStart() {
        return weekStart;
    }

    public void setWeekStart(String weekStart) {
        this.weekStart = weekStart;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(Integer weekNumber) {
        this.weekNumber = weekNumber;
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
