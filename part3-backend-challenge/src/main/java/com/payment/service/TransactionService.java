package com.payment.service;

import com.payment.dto.*;
import com.payment.entity.TransactionMaster;
import com.payment.entity.TransactionDetail;
import com.payment.repository.TransactionRepository;
import com.payment.repository.TransactionDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Function;

/**
 * Service layer for Transaction operations
 * Handles business logic, filtering, and pagination
 */
@Singleton
public class TransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final TransactionDetailRepository transactionDetailRepository;

    public TransactionService(TransactionRepository transactionRepository,
            TransactionDetailRepository transactionDetailRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionDetailRepository = transactionDetailRepository;
    }

    /**
     * Get merchant transactions with filtering, pagination, and summary
     */
    public MerchantTransactionResponse getMerchantTransactions(String merchantId, TransactionRequest request) {
        LOG.info("Fetching transactions for merchant: {}", merchantId);

        // Prepare date range
        Date startDate = request.getStartDate() != null ? Date.valueOf(request.getStartDate()) : null;
        Date endDate = request.getEndDate() != null ? Date.valueOf(request.getEndDate()) : null;

        // Prepare page parameters
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;
        Pageable pageable = Pageable.from(page, size);

        // Apply filters and get paginated results
        Page<TransactionMaster> transactionPage;

        if (startDate != null && endDate != null && request.getStatus() != null) {
            // Filter by date range and status
            transactionPage = transactionRepository.findByMerchantIdAndTxnDateBetweenAndStatusEquals(
                    merchantId, startDate, endDate, request.getStatus(), pageable);
        } else if (startDate != null && endDate != null) {
            // Filter by date range only
            transactionPage = transactionRepository.findByMerchantIdAndTxnDateBetween(
                    merchantId, startDate, endDate, pageable);
        } else if (request.getStatus() != null) {
            // Filter by status only
            transactionPage = transactionRepository.findByMerchantIdAndStatusEquals(
                    merchantId, request.getStatus(), pageable);
        } else {
            // No filters
            transactionPage = transactionRepository.findByMerchantId(merchantId, pageable);
        }

        // Calculate summary
        TransactionSummaryDTO summary = calculateSummary(merchantId, startDate, endDate, request.getStatus());

        // Convert transactions to response DTOs
        List<TransactionResponse> transactionResponses = transactionPage.getContent().stream()
                .map(this::convertToTransactionResponse)
                .collect(Collectors.toList());

        // Prepare date range for response
        Instant startInstant = startDate != null ? startDate.toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant()
                : null;
        Instant endInstant = endDate != null
                ? endDate.toLocalDate().atStartOfDay(ZoneOffset.UTC).plusDays(1).minusNanos(1).toInstant()
                : null;

        if (startDate != null && endDate != null) {
            // If both dates are provided, set end to end of day (23:59:59)
            endInstant = endDate.toLocalDate().atStartOfDay(ZoneOffset.UTC).plusDays(1).minusNanos(1).toInstant();
        } else if (startDate != null) {
            // If only start date is provided, set end to end of today
            endInstant = LocalDate.now().atStartOfDay().plusDays(1).minusNanos(1).atZone(ZoneOffset.UTC).toInstant();
        } else if (endDate != null) {
            // If only end date is provided, set start to beginning of epoch
            startInstant = LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant();
        }

        // Build response
        return MerchantTransactionResponse.builder()
                .merchantId(merchantId)
                .dateRange(DateRangeDTO.builder()
                        .start(startInstant)
                        .end(endInstant)
                        .build())
                .summary(summary)
                .transactions(transactionResponses)
                .pagination(PaginationDTO.builder()
                        .page(page)
                        .size(size)
                        .totalPages(transactionPage.getTotalPages())
                        .totalElements(transactionPage.getTotalSize())
                        .build())
                .build();
    }

    /**
     * Calculate transaction summary
     */
    private TransactionSummaryDTO calculateSummary(String merchantId, Date startDate, Date endDate, String status) {
        Long totalTransactions;
        BigDecimal totalAmount;
        List<String> distinctStatuses;

        if (startDate != null && endDate != null && status != null) {
            // Filter by date range and status
            totalTransactions = transactionRepository.countByMerchantIdAndTxnDateBetweenAndStatus(
                    merchantId, startDate, endDate, status);
            totalAmount = transactionRepository.sumAmountByMerchantIdAndTxnDateBetween(merchantId, startDate, endDate);
            distinctStatuses = transactionRepository.findDistinctStatusByMerchantIdAndTxnDateBetween(merchantId,
                    startDate, endDate);
        } else if (startDate != null && endDate != null) {
            // Filter by date range only
            totalTransactions = transactionRepository.countByMerchantIdAndTxnDateBetween(merchantId, startDate,
                    endDate);
            totalAmount = transactionRepository.sumAmountByMerchantIdAndTxnDateBetween(merchantId, startDate, endDate);
            distinctStatuses = transactionRepository.findDistinctStatusByMerchantIdAndTxnDateBetween(merchantId,
                    startDate, endDate);
        } else if (status != null) {
            // Filter by status only
            totalTransactions = transactionRepository.countByMerchantIdAndStatus(merchantId, status);
            totalAmount = transactionRepository.sumAmountByMerchantId(merchantId); // Total for merchant, not just
                                                                                   // status
            distinctStatuses = transactionRepository.findDistinctStatusByMerchantId(merchantId);
        } else {
            // No filters
            totalTransactions = transactionRepository.countByMerchantId(merchantId);
            totalAmount = transactionRepository.sumAmountByMerchantId(merchantId);
            distinctStatuses = transactionRepository.findDistinctStatusByMerchantId(merchantId);
        }

        // Count transactions by status
        Map<String, Long> byStatus = distinctStatuses.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        statusValue -> {
                            if (startDate != null && endDate != null) {
                                return transactionRepository.countByMerchantIdAndTxnDateBetweenAndStatus(merchantId,
                                        startDate, endDate, statusValue);
                            } else if (status != null) {
                                return transactionRepository.countByMerchantIdAndStatus(merchantId, statusValue);
                            } else {
                                return transactionRepository.countByMerchantIdAndStatus(merchantId, statusValue);
                            }
                        }));

        // Handle case where there are no transactions
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }

        return TransactionSummaryDTO.builder()
                .totalTransactions(totalTransactions != null ? totalTransactions : 0)
                .totalAmount(totalAmount)
                .currency("USD") // Default currency, can be configured
                .byStatus(byStatus)
                .build();
    }

    /**
     * Convert TransactionMaster entity to TransactionResponse DTO
     */
    private TransactionResponse convertToTransactionResponse(TransactionMaster transaction) {
        // Get transaction details for this transaction
        List<TransactionDetail> details = transactionDetailRepository.findByMasterTxnId(transaction.getTxnId());

        List<TransactionDetailDTO> detailDTOs = details.stream()
                .map(detail -> TransactionDetailDTO.builder()
                        .detailId(detail.getTxnDetailId())
                        .type(detail.getDetailType())
                        .amount(detail.getAmount())
                        .description(detail.getDescription())
                        .build())
                .collect(Collectors.toList());

        // Use local transaction date time if available, otherwise use created at
        Instant timestamp = transaction.getLocalTxnDateTime();
        if (timestamp == null) {
            timestamp = transaction.getCreatedAt();
        }

        return TransactionResponse.builder()
                .txnId(transaction.getTxnId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .status(transaction.getStatus())
                .timestamp(timestamp)
                .cardType(transaction.getCardType())
                .cardLast4(transaction.getCardLast4())
                .acquirer("Global Payment Services") // Placeholder - could be retrieved from another table
                .issuer("Visa Worldwide") // Placeholder - could be retrieved from another table
                .details(detailDTOs)
                .build();
    }
}
