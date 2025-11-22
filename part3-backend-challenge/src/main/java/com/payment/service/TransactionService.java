package com.payment.service;

import com.payment.dto.TransactionResponse;
import com.payment.dto.TransactionResponse.TransactionDetailDTO;
import com.payment.dto.TransactionResponse.TransactionSummary;
import com.payment.entity.TransactionMaster;
import com.payment.mapper.TransactionMapper;
import com.payment.repository.TransactionRepository;
import com.payment.util.ValidationUtil;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Transaction operations
 * Handles business logic, filtering, and pagination
 */
@Singleton
public class TransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final MetricsService metricsService;

    public TransactionService(TransactionRepository transactionRepository, TransactionMapper transactionMapper, MetricsService metricsService) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.metricsService = metricsService;
    }

    /**
     * Search transactions with filters and pagination, returning DTOs with summary
     * @param merchantId The merchant ID to search for
     * @param startDate Start date for filtering (optional)
     * @param endDate End date for filtering (optional)
     * @param status Status for filtering (optional)
     * @param currency Currency for filtering (optional)
     * @param minAmount Minimum amount for filtering (optional)
     * @param maxAmount Maximum amount for filtering (optional)
     * @param pageable Pagination parameters
     * @return TransactionResponse with transactions and summary
     */
    @Cacheable("transactions")
    public TransactionResponse searchTransactions(
            String merchantId,
            LocalDate startDate,
            LocalDate endDate,
            String status,
            String currency,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Pageable pageable
    ) {
        LOG.info("Searching transactions for merchant: {}, page: {}, size: {}", 
                merchantId, pageable.getNumber(), pageable.getSize());

        // Start timing the operation
        var timerSample = metricsService.startTransactionSearchTimer();

        try {
            // Validate inputs
            ValidationUtil.validateMerchantId(merchantId);
            ValidationUtil.validatePagination(pageable.getNumber(), pageable.getSize());
            ValidationUtil.validateFilters(status, currency, null); // cardType not used in this method

            // Convert LocalDate to Date for database queries
            Date sqlStartDate = startDate != null ? Date.valueOf(startDate) : null;
            Date sqlEndDate = endDate != null ? Date.valueOf(endDate) : null;

            // Fetch paginated transactions with filters
            Page<TransactionMaster> transactionPage = transactionRepository.findWithFilters(
                    merchantId,
                    sqlStartDate,
                    sqlEndDate,
                    status,
                    currency,
                    minAmount,
                    maxAmount,
                    pageable
            );

            // Calculate summary statistics
            TransactionSummary summary = calculateSummary(merchantId, startDate, endDate, status, currency, minAmount, maxAmount);

            // Convert entities to DTOs
            List<TransactionDetailDTO> transactionDTOs = transactionPage.getContent().stream()
                    .map(transactionMapper::toTransactionDetailDTO)
                    .toList();

            LOG.info("Found {} transactions for merchant: {}", transactionPage.getTotalSize(), merchantId);

            // Record metrics
            metricsService.recordTransactionSearch(merchantId, transactionPage.getContent().size());
            metricsService.recordTransactionSearchDuration(timerSample, merchantId);

            return TransactionResponse.builder()
                    .transactions(transactionDTOs)
                    .summary(summary)
                    .build();
        } catch (Exception e) {
            metricsService.recordError("search_error", "transaction_search");
            LOG.error("Error searching transactions for merchant: {}", merchantId, e);
            throw e;
        }
    }

    /**
     * Calculate summary statistics for transactions
     */
    private TransactionSummary calculateSummary(
            String merchantId,
            LocalDate startDate,
            LocalDate endDate,
            String status,
            String currency,
            BigDecimal minAmount,
            BigDecimal maxAmount
    ) {
        Date sqlStartDate = startDate != null ? Date.valueOf(startDate) : null;
        Date sqlEndDate = endDate != null ? Date.valueOf(endDate) : null;

        Optional<Object[]> summaryResult = transactionRepository.calculateSummary(
                merchantId,
                sqlStartDate,
                sqlEndDate,
                status,
                currency,
                minAmount,
                maxAmount
        );

        if (summaryResult.isPresent() && summaryResult.get()[0] != null) {
            Object[] result = summaryResult.get();
            Long totalTransactions = result[0] != null ? ((Number) result[0]).longValue() : 0L;
            BigDecimal totalAmount = result[1] != null ? (BigDecimal) result[1] : BigDecimal.ZERO;
            BigDecimal averageAmount = result[2] != null ? (BigDecimal) result[2] : BigDecimal.ZERO;
            BigDecimal minAmountResult = result[3] != null ? (BigDecimal) result[3] : BigDecimal.ZERO;
            BigDecimal maxAmountResult = result[4] != null ? (BigDecimal) result[4] : BigDecimal.ZERO;

            return TransactionSummary.builder()
                    .totalTransactions(totalTransactions)
                    .totalAmount(totalAmount)
                    .averageAmount(averageAmount)
                    .minAmount(minAmountResult)
                    .maxAmount(maxAmountResult)
                    .build();
        } else {
            return TransactionSummary.builder()
                    .totalTransactions(0L)
                    .totalAmount(BigDecimal.ZERO)
                    .averageAmount(BigDecimal.ZERO)
                    .minAmount(BigDecimal.ZERO)
                    .maxAmount(BigDecimal.ZERO)
                    .build();
        }
    }

    /**
     * Get all transactions for a merchant with summary (without pagination)
     */
    public TransactionResponse getAllTransactionsByMerchant(String merchantId) {
        ValidationUtil.validateMerchantId(merchantId);
        
        List<TransactionMaster> transactions = transactionRepository.findByMerchantId(merchantId);
        
        List<TransactionDetailDTO> transactionDTOs = transactions.stream()
                .map(transactionMapper::toTransactionDetailDTO)
                .toList();
        
        // Calculate summary for all transactions
        TransactionSummary summary = calculateSummary(merchantId, null, null, null, null, null, null);

        return TransactionResponse.builder()
                .transactions(transactionDTOs)
                .summary(summary)
                .build();
    }

    /**
     * Create a new transaction
     */
    public TransactionDetailDTO createTransaction(TransactionMaster transaction) {
        ValidationUtil.validateMerchantId(transaction.getMerchantId());
        
        TransactionMaster saved = transactionRepository.save(transaction);
        TransactionDetailDTO result = transactionMapper.toTransactionDetailDTO(saved);
        
        // Record metrics
        metricsService.recordTransactionCreation(transaction.getMerchantId(), transaction.getStatus());
        
        return result;
    }

    /**
     * Find a transaction by ID
     */
    public TransactionDetailDTO findById(Long txnId) {
        return transactionRepository.findById(txnId)
                .map(transactionMapper::toTransactionDetailDTO)
                .orElse(null);
    }
}
