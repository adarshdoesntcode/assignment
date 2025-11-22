package com.payment.controller;

import com.payment.dto.TransactionResponse;
import com.payment.dto.TransactionResponse.TransactionDetailDTO;
import com.payment.entity.TransactionMaster;
import com.payment.service.TransactionService;
import io.micronaut.cache.annotation.CacheInvalidate;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Transaction Controller
 * Handles HTTP requests for transaction operations
 */
@Controller("/api/v1/merchants")
@Tag(name = "Transactions")
public class TransactionController {

    private final TransactionService transactionService;
    
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Get("/{merchantId}/transactions")
    @Operation(
        summary = "Search merchant transactions",
        description = "Returns filtered and paginated list of transactions for a merchant based on search criteria with summary"
    )
    @ExecuteOn(TaskExecutors.IO)
    public HttpResponse<TransactionResponse> searchTransactions(
            @PathVariable String merchantId,
            @Parameter(description = "Page number (0-indexed)") @QueryValue(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size") @QueryValue(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Start date filter (YYYY-MM-DD)") @QueryValue(required = false) LocalDate startDate,
            @Parameter(description = "End date filter (YYYY-MM-DD)") @QueryValue(required = false) LocalDate endDate,
            @Parameter(description = "Status filter") @QueryValue(required = false) String status,
            @Parameter(description = "Currency filter") @QueryValue(required = false) String currency,
            @Parameter(description = "Minimum amount filter") @QueryValue(required = false) BigDecimal minAmount,
            @Parameter(description = "Maximum amount filter") @QueryValue(required = false) BigDecimal maxAmount
    ) {
        Pageable pageable = Pageable.from(page, size);
        
        TransactionResponse response = transactionService.searchTransactions(
                merchantId,
                startDate,
                endDate,
                status,
                currency,
                minAmount,
                maxAmount,
                pageable
        );
        
        return HttpResponse.ok(response);
    }

    @Get("/{merchantId}/transactions/{txnId}")
    @Operation(
        summary = "Get transaction by ID",
        description = "Returns a specific transaction by its ID"
    )
    public HttpResponse<TransactionDetailDTO> getTransactionById(
            @PathVariable String merchantId,
            @PathVariable Long txnId
    ) {
        TransactionDetailDTO transaction = transactionService.findById(txnId);
        if (transaction == null || !merchantId.equals(transaction.getMerchantId())) {
            return HttpResponse.notFound();
        }
        return HttpResponse.ok(transaction);
    }

    @Post("/{merchantId}/transactions")
    @Operation(
        summary = "Create new transaction",
        description = "Creates a new transaction for a merchant"
    )
    @CacheInvalidate(cacheNames = "transactions")
    public HttpResponse<TransactionDetailDTO> createTransaction(
            @PathVariable String merchantId,
            @Body TransactionMaster transaction
    ) {
        transaction.setMerchantId(merchantId);
        TransactionDetailDTO created = transactionService.createTransaction(transaction);
        return HttpResponse.created(created);
    }
}
