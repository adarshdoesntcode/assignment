package com.payment.service;

import com.payment.dto.SearchFilter;
import com.payment.dto.TransactionResponseDTO;
import com.payment.entity.TransactionMaster;
import com.payment.mapper.TransactionMapper;
import com.payment.repository.TransactionRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Transaction operations
 * Handles business logic, filtering, and pagination
 */
@Singleton
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    /**
     * Search transactions with filters and pagination, returning DTOs
     * @param merchantId The merchant ID to search for
     * @param searchFilters List of search filters to apply
     * @param pageable Pagination parameters
     * @return Page of TransactionResponseDTO
     */
    public Page<TransactionResponseDTO> searchTransactions(
            String merchantId,
            List<SearchFilter> searchFilters,
            Pageable pageable
    ) {
        // Fetch all transactions for the merchant
        List<TransactionMaster> allTransactions = transactionRepository.findByMerchantId(merchantId);

        // Apply search filters
        List<TransactionMaster> filteredTransactions = allTransactions;
        if (searchFilters != null && !searchFilters.isEmpty()) {
            filteredTransactions = allTransactions.stream()
                    .filter(transaction -> applyFilters(transaction, searchFilters))
                    .collect(Collectors.toList());
        }

        // Apply pagination
        int totalSize = filteredTransactions.size();
        int pageNumber = pageable.getNumber();
        int pageSize = pageable.getSize();
        int fromIndex = pageNumber * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalSize);

        List<TransactionMaster> paginatedTransactions;
        if (fromIndex < totalSize) {
            paginatedTransactions = filteredTransactions.subList(fromIndex, toIndex);
        } else {
            paginatedTransactions = List.of();
        }

        // Convert to DTOs
        List<TransactionResponseDTO> dtos = paginatedTransactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());

        // Return Micronaut Page with DTOs
        return Page.of(dtos, pageable, totalSize);
    }

    /**
     * Apply all filters to a transaction (AND logic)
     * @param transaction The transaction to check
     * @param filters List of filters to apply
     * @return true if transaction matches all filters
     */
    private boolean applyFilters(TransactionMaster transaction, List<SearchFilter> filters) {
        for (SearchFilter filter : filters) {
            if (!matchesFilter(transaction, filter)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if a transaction matches a single filter
     * @param transaction The transaction to check
     * @param filter The filter to apply
     * @return true if transaction matches the filter
     */
    private boolean matchesFilter(TransactionMaster transaction, SearchFilter filter) {
        String field = filter.getField();
        String condition = filter.getCondition();
        String value = filter.getValue();

        if (field == null || condition == null || value == null) {
            return true;
        }

        return switch (field) {
            case "txnDate" -> matchDateField(transaction.getTxnDate(), condition, value);
            case "status" -> matchStringField(transaction.getStatus(), condition, value);
            case "amount" -> matchNumericField(transaction.getAmount(), condition, value);
            case "currency" -> matchStringField(transaction.getCurrency(), condition, value);
            case "cardType" -> matchStringField(transaction.getCardType(), condition, value);
            case "cardLast4" -> matchStringField(transaction.getCardLast4(), condition, value);
            case "authCode" -> matchStringField(transaction.getAuthCode(), condition, value);
            case "responseCode" -> matchStringField(transaction.getResponseCode(), condition, value);
            case "merchantId" -> matchStringField(transaction.getMerchantId(), condition, value);
            case "gpAcquirerId" -> matchLongField(transaction.getGpAcquirerId(), condition, value);
            case "gpIssuerId" -> matchLongField(transaction.getGpIssuerId(), condition, value);
            default -> true; // Unknown field, don't filter
        };
    }

    /**
     * Match string field with condition
     */
    private boolean matchStringField(String fieldValue, String condition, String filterValue) {
        if (fieldValue == null) {
            return false;
        }

        return switch (condition) {
            case "equals" -> fieldValue.equalsIgnoreCase(filterValue);
            case "notEquals" -> !fieldValue.equalsIgnoreCase(filterValue);
            case "contains" -> fieldValue.toLowerCase().contains(filterValue.toLowerCase());
            default -> true;
        };
    }

    /**
     * Match numeric field (BigDecimal) with condition
     */
    private boolean matchNumericField(BigDecimal fieldValue, String condition, String filterValue) {
        if (fieldValue == null) {
            return false;
        }

        try {
            BigDecimal filterAmount = new BigDecimal(filterValue);
            return switch (condition) {
                case "equals" -> fieldValue.compareTo(filterAmount) == 0;
                case "notEquals" -> fieldValue.compareTo(filterAmount) != 0;
                case "greaterThan" -> fieldValue.compareTo(filterAmount) > 0;
                case "lessThan" -> fieldValue.compareTo(filterAmount) < 0;
                case "greaterThanOrEqual" -> fieldValue.compareTo(filterAmount) >= 0;
                case "lessThanOrEqual" -> fieldValue.compareTo(filterAmount) <= 0;
                default -> true;
            };
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Match Long field with condition
     */
    private boolean matchLongField(Long fieldValue, String condition, String filterValue) {
        if (fieldValue == null) {
            return false;
        }

        try {
            Long filterLong = Long.parseLong(filterValue);
            return switch (condition) {
                case "equals" -> fieldValue.equals(filterLong);
                case "notEquals" -> !fieldValue.equals(filterLong);
                case "greaterThan" -> fieldValue > filterLong;
                case "lessThan" -> fieldValue < filterLong;
                case "greaterThanOrEqual" -> fieldValue >= filterLong;
                case "lessThanOrEqual" -> fieldValue <= filterLong;
                default -> true;
            };
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Match date field with condition
     */
    private boolean matchDateField(Date fieldValue, String condition, String filterValue) {
        if (fieldValue == null) {
            return false;
        }

        try {
            LocalDate filterDate = LocalDate.parse(filterValue, DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate txnLocalDate = fieldValue.toLocalDate();

            return switch (condition) {
                case "equals" -> txnLocalDate.isEqual(filterDate);
                case "notEquals" -> !txnLocalDate.isEqual(filterDate);
                case "greaterThan" -> txnLocalDate.isAfter(filterDate);
                case "lessThan" -> txnLocalDate.isBefore(filterDate);
                case "greaterThanOrEqual" -> txnLocalDate.isAfter(filterDate) || txnLocalDate.isEqual(filterDate);
                case "lessThanOrEqual" -> txnLocalDate.isBefore(filterDate) || txnLocalDate.isEqual(filterDate);
                default -> true;
            };
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Create a new transaction
     */
    public TransactionResponseDTO createTransaction(TransactionMaster transaction) {
        TransactionMaster saved = transactionRepository.save(transaction);
        return transactionMapper.toDTO(saved);
    }

    /**
     * Find a transaction by ID
     */
    public TransactionResponseDTO findById(Long txnId) {
        return transactionRepository.findById(txnId)
                .map(transactionMapper::toDTO)
                .orElse(null);
    }

    /**
     * Get all transactions for a merchant (without filters)
     */
    public List<TransactionResponseDTO> getAllTransactionsByMerchant(String merchantId) {
        return transactionRepository.findByMerchantId(merchantId).stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }
}
