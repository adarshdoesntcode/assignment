package com.payment.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Transaction response object with details")
public class TransactionResponse {

    @Schema(description = "Transaction ID", example = "98765")
    private Long txnId;

    @Schema(description = "Transaction amount", example = "150.00")
    private BigDecimal amount;

    @Schema(description = "Currency code", example = "USD")
    private String currency;

    @Schema(description = "Transaction status", example = "completed")
    private String status;

    @Schema(description = "Transaction timestamp", example = "2025-11-18T14:32:15Z")
    private Instant timestamp;

    @Schema(description = "Card type", example = "VISA")
    private String cardType;

    @Schema(description = "Last 4 digits of card", example = "4242")
    private String cardLast4;

    @Schema(description = "Acquirer name", example = "Global Payment Services")
    private String acquirer;

    @Schema(description = "Issuer name", example = "Visa Worldwide")
    private String issuer;

    @Schema(description = "Transaction details")
    private List<TransactionDetailDTO> details;
}