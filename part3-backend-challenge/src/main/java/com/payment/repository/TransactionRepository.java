package com.payment.repository;

import com.payment.entity.TransactionMaster;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repository for TransactionMaster entities with custom query methods.
 */
@Repository
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface TransactionRepository extends CrudRepository<TransactionMaster, Long> {

    /**
     * Find all transactions for a specific merchant ID
     */
    List<TransactionMaster> findByMerchantId(String merchantId);

    /**
     * Find paginated transactions for a specific merchant ID
     */
    Page<TransactionMaster> findByMerchantId(String merchantId, Pageable pageable);

    /**
     * Find transactions for a specific merchant with date range
     */
    List<TransactionMaster> findByMerchantIdAndTxnDateBetween(String merchantId, Date startDate, Date endDate);

    /**
     * Find paginated transactions for a specific merchant with date range
     */
    Page<TransactionMaster> findByMerchantIdAndTxnDateBetween(String merchantId, Date startDate, Date endDate, Pageable pageable);

    /**
     * Custom query method with dynamic filtering using @Query annotation
     */
    @Query("SELECT tm FROM TransactionMaster tm WHERE tm.merchantId = :merchantId " +
           "AND (:startDate IS NULL OR tm.txnDate >= :startDate) " +
           "AND (:endDate IS NULL OR tm.txnDate <= :endDate) " +
           "AND (:status IS NULL OR LOWER(tm.status) = LOWER(:status)) " +
           "AND (:currency IS NULL OR LOWER(tm.currency) = LOWER(:currency)) " +
           "AND (:minAmount IS NULL OR tm.amount >= :minAmount) " +
           "AND (:maxAmount IS NULL OR tm.amount <= :maxAmount)")
    Page<TransactionMaster> findWithFilters(
        String merchantId,
        Date startDate,
        Date endDate,
        String status,
        String currency,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        Pageable pageable
    );

    /**
     * Custom query to calculate summary statistics for a merchant
     */
    @Query("SELECT COUNT(tm) as totalTransactions, " +
           "SUM(tm.amount) as totalAmount, " +
           "AVG(tm.amount) as averageAmount, " +
           "MIN(tm.amount) as minAmount, " +
           "MAX(tm.amount) as maxAmount " +
           "FROM TransactionMaster tm " +
           "WHERE tm.merchantId = :merchantId " +
           "AND (:startDate IS NULL OR tm.txnDate >= :startDate) " +
           "AND (:endDate IS NULL OR tm.txnDate <= :endDate) " +
           "AND (:status IS NULL OR LOWER(tm.status) = LOWER(:status)) " +
           "AND (:currency IS NULL OR LOWER(tm.currency) = LOWER(:currency)) " +
           "AND (:minAmount IS NULL OR tm.amount >= :minAmount) " +
           "AND (:maxAmount IS NULL OR tm.amount <= :maxAmount)")
    Optional<Object[]> calculateSummary(
        String merchantId,
        Date startDate,
        Date endDate,
        String status,
        String currency,
        BigDecimal minAmount,
        BigDecimal maxAmount
    );
}
