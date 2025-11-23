package com.payment.repository.projection;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class StatusCountProjection {
    private String status;
    private Long count;

    public StatusCountProjection() {
    }

    public StatusCountProjection(String status, Long count) {
        this.status = status;
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
