package com.payment.repository.projection;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class DayOfWeekCountProjection {
    private Integer dow;
    private Long count;

    public DayOfWeekCountProjection() {
    }

    public DayOfWeekCountProjection(Integer dow, Long count) {
        this.dow = dow;
        this.count = count;
    }

    public Integer getDow() {
        return dow;
    }

    public void setDow(Integer dow) {
        this.dow = dow;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
