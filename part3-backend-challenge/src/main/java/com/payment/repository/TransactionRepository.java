package com.payment.repository;

import com.payment.entity.TransactionMaster;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository for TransactionMaster entities.
 * 
 * TODO: Add custom query methods for:
 * - Finding transactions by merchant ID with date range
 * - Paginated queries
 * - Aggregation queries for summary calculation
 * - Join queries with transaction details
 */
@Repository
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface TransactionRepository extends CrudRepository<TransactionMaster, Long> {

    // Example: Basic finder method (provided)
    List<TransactionMaster> findByMerchantId(String merchantId);

    // Find transactions by merchant ID with date range and status filtering
    List<TransactionMaster> findByMerchantIdAndTxnDateBetweenAndStatusEquals(String merchantId, java.sql.Date startDate,
            java.sql.Date endDate, String status);

    List<TransactionMaster> findByMerchantIdAndTxnDateBetween(String merchantId, java.sql.Date startDate,
            java.sql.Date endDate);

    List<TransactionMaster> findByMerchantIdAndStatusEquals(String merchantId, String status);

    // Find transactions with pagination
    Page<TransactionMaster> findByMerchantId(String merchantId, Pageable pageable);

    Page<TransactionMaster> findByMerchantIdAndTxnDateBetweenAndStatusEquals(String merchantId, java.sql.Date startDate,
            java.sql.Date endDate, String status, Pageable pageable);

    Page<TransactionMaster> findByMerchantIdAndTxnDateBetween(String merchantId, java.sql.Date startDate,
            java.sql.Date endDate, Pageable pageable);

    Page<TransactionMaster> findByMerchantIdAndStatusEquals(String merchantId, String status, Pageable pageable);

    // Aggregation methods for summary
    @Query("SELECT COUNT(*) FROM operators.transaction_master WHERE merchant_id = :merchantId")
    Long countByMerchantId(String merchantId);

    @Query("SELECT COUNT(*) FROM operators.transaction_master WHERE merchant_id = :merchantId AND status = :status")
    Long countByMerchantIdAndStatus(String merchantId, String status);

    @Query("SELECT COUNT(*) FROM operators.transaction_master WHERE merchant_id = :merchantId AND txn_date BETWEEN :startDate AND :endDate")
    Long countByMerchantIdAndTxnDateBetween(String merchantId, java.sql.Date startDate, java.sql.Date endDate);

    @Query("SELECT COUNT(*) FROM operators.transaction_master WHERE merchant_id = :merchantId AND txn_date BETWEEN :startDate AND :endDate AND status = :status")
    Long countByMerchantIdAndTxnDateBetweenAndStatus(String merchantId, java.sql.Date startDate, java.sql.Date endDate,
            String status);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM operators.transaction_master WHERE merchant_id = :merchantId")
    BigDecimal sumAmountByMerchantId(String merchantId);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM operators.transaction_master WHERE merchant_id = :merchantId AND txn_date BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByMerchantIdAndTxnDateBetween(String merchantId, java.sql.Date startDate,
            java.sql.Date endDate);

    @Query("SELECT DISTINCT status FROM operators.transaction_master WHERE merchant_id = :merchantId")
    List<String> findDistinctStatusByMerchantId(String merchantId);

    @Query("SELECT DISTINCT status FROM operators.transaction_master WHERE merchant_id = :merchantId AND txn_date BETWEEN :startDate AND :endDate")
    List<String> findDistinctStatusByMerchantIdAndTxnDateBetween(String merchantId, java.sql.Date startDate,
            java.sql.Date endDate);
}
