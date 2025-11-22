package com.payment.service;

import com.payment.dto.*;
import com.payment.entity.TransactionMaster;
import com.payment.entity.TransactionDetail;
import com.payment.mapper.TransactionMapper;
import com.payment.repository.TransactionRepository;
import com.payment.repository.TransactionDetailRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Service layer for Transaction operations
 * Handles business logic, filtering, and pagination
 */
@Singleton
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionDetailRepository transactionDetailRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository, 
                             TransactionDetailRepository transactionDetailRepository,
                             TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionDetailRepository = transactionDetailRepository;
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

    /**
     * Get merchant transactions with filtering, pagination, and summary
     */
    public MerchantTransactionResponse getMerchantTransactions(String merchantId, TransactionRequest request) {
        // Validate merchant exists
        List<TransactionMaster> allMerchantTransactions = transactionRepository.findByMerchantId(merchantId);
        if (allMerchantTransactions.isEmpty()) {
            // For this implementation, we'll proceed with empty data instead of throwing an exception
            // as it's valid to have no transactions
        }

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
        Instant startInstant = startDate != null ? startDate.toInstant().atZone(ZoneOffset.UTC).toInstant() : null;
        Instant endInstant = endDate != null ? endDate.toInstant().atZone(ZoneOffset.UTC).plusDays(1).minusNanos(1).toInstant() : null;
        
        if (startDate != null && endDate != null) {
            // If both dates are provided, set end to end of day (23:59:59)
            endInstant = endDate.toInstant().atZone(ZoneOffset.UTC).plusDays(1).minusNanos(1).toInstant();
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
            distinctStatuses = transactionRepository.findDistinctStatusByMerchantIdAndTxnDateBetween(merchantId, startDate, endDate);
        } else if (startDate != null && endDate != null) {
            // Filter by date range only
            totalTransactions = transactionRepository.countByMerchantIdAndTxnDateBetween(merchantId, startDate, endDate);
            totalAmount = transactionRepository.sumAmountByMerchantIdAndTxnDateBetween(merchantId, startDate, endDate);
            distinctStatuses = transactionRepository.findDistinctStatusByMerchantIdAndTxnDateBetween(merchantId, startDate, endDate);
        } else if (status != null) {
            // Filter by status only
            totalTransactions = transactionRepository.countByMerchantIdAndStatus(merchantId, status);
            totalAmount = transactionRepository.sumAmountByMerchantId(merchantId); // Total for merchant, not just status
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
                        return transactionRepository.countByMerchantIdAndTxnDateBetweenAndStatus(merchantId, startDate, endDate, statusValue);
                    } else if (status != null) {
                        return transactionRepository.countByMerchantIdAndStatus(merchantId, statusValue);
                    } else {
                        return transactionRepository.countByMerchantIdAndStatus(merchantId, statusValue);
                    }
                }
            ));

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
