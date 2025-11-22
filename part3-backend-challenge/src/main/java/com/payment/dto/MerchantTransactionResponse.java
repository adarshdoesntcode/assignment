package com.payment.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Response object for merchant transactions with summary and pagination")
public class MerchantTransactionResponse {

    @Schema(description = "Merchant ID", example = "MCH-00001")
    private String merchantId;

    @Schema(description = "Date range for the transactions")
    private DateRangeDTO dateRange;

    @Schema(description = "Transaction summary")
    private TransactionSummaryDTO summary;

    @Schema(description = "List of transactions")
    private List<TransactionResponse> transactions;

    @Schema(description = "Pagination information")
    private PaginationDTO pagination;
}