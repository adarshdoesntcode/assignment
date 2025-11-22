package com.payment.controller;

import io.micronaut.http.annotation.Controller;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.annotation.Header;
import io.swagger.v3.oas.annotations.Operation;
import com.payment.entity.TransactionMaster;
import com.payment.dto.TransactionResponseDTO;
import com.payment.dto.TransactionSearchRequest;
import com.payment.dto.TransactionRequest;
import com.payment.dto.MerchantTransactionResponse;
import com.payment.service.TransactionService;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.Optional;

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

    @Post("/{merchantId}/transactions")
    @Operation(
        summary = "Search merchant transactions",
        description = "Returns filtered and paginated list of transactions for a merchant based on search criteria"
    )
    public HttpResponse<Page<TransactionResponseDTO>> searchTransactions(
            @PathVariable String merchantId,
            @QueryValue Optional<Integer> page,
            @QueryValue Optional<Integer> size,
            @Body TransactionSearchRequest searchRequest
    ) {
        int pageNumber = page.orElse(0);
        int pageSize = size.orElse(10);
        Pageable pageable = Pageable.from(pageNumber, pageSize);
        
        Page<TransactionResponseDTO> transactionPage = transactionService.searchTransactions(
                merchantId,
                searchRequest.getSearchFilters(),
                pageable
        );
        
        return HttpResponse.ok(transactionPage);
    }

    @Get("/{merchantId}/transactions")
    @Operation(
        summary = "Get merchant transactions",
        description = "Returns filtered and paginated list of transactions for a merchant with summary information"
    )
    public HttpResponse<MerchantTransactionResponse> getTransactions(
            @PathVariable String merchantId,
            @QueryValue(defaultValue = "0") @Valid Integer page,
            @QueryValue(defaultValue = "20") @Valid Integer size,
            @QueryValue @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "startDate must be in ISO date format (YYYY-MM-DD)") String startDate,
            @QueryValue @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "endDate must be in ISO date format (YYYY-MM-DD)") String endDate,
            @QueryValue String status) {
        
        TransactionRequest request = TransactionRequest.builder()
                .page(page)
                .size(size)
                .startDate(startDate != null ? LocalDate.parse(startDate) : null)
                .endDate(endDate != null ? LocalDate.parse(endDate) : null)
                .status(status)
                .build();
        
        MerchantTransactionResponse response = transactionService.getMerchantTransactions(merchantId, request);
        return HttpResponse.ok(response);
    }

    @Post("/{merchantId}/transactions/create")
    @Operation(
        summary = "Create new transaction",
        description = "Creates a new transaction for a merchant"
    )
    public HttpResponse<TransactionResponseDTO> createTransaction(
            @PathVariable String merchantId,
            @Body TransactionMaster transaction
    ) {
        transaction.setMerchantId(merchantId);
        TransactionResponseDTO created = transactionService.createTransaction(transaction);
        return HttpResponse.created(created);
    }
}
