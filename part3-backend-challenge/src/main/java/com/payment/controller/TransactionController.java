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
import com.payment.service.TransactionService;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;

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
        description = "Returns list of all transactions for a merchant"
    )
    public HttpResponse<?> getTransactions(@PathVariable String merchantId) {
        var transactions = transactionService.getAllTransactionsByMerchant(merchantId);
        return HttpResponse.ok(transactions);
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
