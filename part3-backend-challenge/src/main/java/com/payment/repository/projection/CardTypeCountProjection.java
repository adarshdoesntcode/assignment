package com.payment.repository.projection;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class CardTypeCountProjection {
    private String cardType;
    private Long count;

    public CardTypeCountProjection() {
    }

    public CardTypeCountProjection(String cardType, Long count) {
        this.cardType = cardType;
        this.count = count;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
