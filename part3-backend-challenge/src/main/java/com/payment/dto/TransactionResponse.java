package com.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Transaction response with summary")
public class TransactionResponse {

    @Schema(description = "List of transaction details")
    private java.util.List<TransactionDetailDTO> transactions;

    @Schema(description = "Summary information for the transactions")
    private TransactionSummary summary;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Serdeable
    public static class TransactionDetailDTO {
        @Schema(description = "Transaction ID", example = "12345")
        private Long txnId;

        @Schema(description = "Merchant ID", example = "MERCHANT123")
        private String merchantId;

        @Schema(description = "Gateway Provider Acquirer ID", example = "1001")
        private Long gpAcquirerId;

        @Schema(description = "Gateway Provider Issuer ID", example = "2001")
        private Long gpIssuerId;

        @Schema(description = "Transaction date", example = "2025-11-16")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate txnDate;

        @Schema(description = "Local transaction date and time", example = "2025-11-16T10:30:00Z")
        private Instant localTxnDateTime;

        @Schema(description = "Transaction amount", example = "150.50")
        private BigDecimal amount;

        @Schema(description = "Currency code", example = "USD")
        private String currency;

        @Schema(description = "Transaction status", example = "APPROVED", allowableValues = {"APPROVED", "DECLINED", "PENDING", "CANCELLED"})
        private String status;

        @Schema(description = "Card type", example = "VISA")
        private String cardType;

        @Schema(description = "Last 4 digits of card", example = "1234")
        private String cardLast4;

        @Schema(description = "Authorization code", example = "AUTH123")
        private String authCode;

        @Schema(description = "Response code", example = "00")
        private String responseCode;

        @Schema(description = "Creation timestamp", example = "2025-11-16T10:30:00Z")
        private Instant createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Serdeable
    public static class TransactionSummary {
        @Schema(description = "Total number of transactions", example = "100")
        private Long totalTransactions;

        @Schema(description = "Total amount of all transactions", example = "15000.75")
        private BigDecimal totalAmount;

        @Schema(description = "Average transaction amount", example = "150.01")
        private BigDecimal averageAmount;

        @Schema(description = "Minimum transaction amount", example = "10.00")
        private BigDecimal minAmount;

        @Schema(description = "Maximum transaction amount", example = "1000.00")
        private BigDecimal maxAmount;
    }
}