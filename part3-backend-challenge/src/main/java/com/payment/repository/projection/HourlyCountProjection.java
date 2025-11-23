package com.payment.repository.projection;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class HourlyCountProjection {
    private Integer hour;
    private Long count;

    public HourlyCountProjection() {
    }

    public HourlyCountProjection(Integer hour, Long count) {
        this.hour = hour;
        this.count = count;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
