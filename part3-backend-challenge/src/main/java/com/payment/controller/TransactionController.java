package com.payment.controller;

import io.micronaut.http.annotation.Controller;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.swagger.v3.oas.annotations.Operation;
import com.payment.dto.TransactionRequest;
import com.payment.dto.MerchantTransactionResponse;
import com.payment.dto.ApiResponse;
import com.payment.service.TransactionService;
import io.micronaut.core.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

/**
 * Transaction Controller
 * Handles HTTP requests for merchant transaction operations
 */
@Controller("/api/v1/merchants")
@Tag(name = "Transactions")
public class TransactionController {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Get("/{merchantId}/transactions")
    @Operation(summary = "Get merchant transactions", description = "Returns filtered and paginated list of transactions for a merchant with summary information")
    public HttpResponse<ApiResponse<MerchantTransactionResponse>> getTransactions(
            @PathVariable String merchantId,
            @QueryValue(defaultValue = "0") @Min(0) Integer page,
            @QueryValue(defaultValue = "20") @Min(1) Integer size,
            @QueryValue @Nullable @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "startDate must be in ISO date format (YYYY-MM-DD)") String startDate,
            @QueryValue @Nullable @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "endDate must be in ISO date format (YYYY-MM-DD)") String endDate,
            @QueryValue @Nullable String status) {

        LOG.info("GET /api/v1/merchants/{}/transactions - page: {}, size: {}, startDate: {}, endDate: {}, status: {}",
                merchantId, page, size, startDate, endDate, status);

        TransactionRequest request = TransactionRequest.builder()
                .page(page)
                .size(size)
                .startDate(startDate != null ? LocalDate.parse(startDate) : null)
                .endDate(endDate != null ? LocalDate.parse(endDate) : null)
                .status(status)
                .build();

        MerchantTransactionResponse response = transactionService.getMerchantTransactions(merchantId, request);

        LOG.info("Returning {} transactions for merchant {}", response.getTransactions().size(), merchantId);

        return HttpResponse.ok(ApiResponse.success("Transactions retrieved successfully", response));
    }
}
