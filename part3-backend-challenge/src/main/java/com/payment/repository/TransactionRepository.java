package com.payment.repository;

import com.payment.entity.TransactionMaster;
import com.payment.repository.projection.*;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.annotation.Repository;

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
        List<TransactionMaster> findByMerchantIdAndTxnDateBetweenAndStatusEquals(String merchantId,
                        java.sql.Date startDate,
                        java.sql.Date endDate, String status);

        List<TransactionMaster> findByMerchantIdAndTxnDateBetween(String merchantId, java.sql.Date startDate,
                        java.sql.Date endDate);

        List<TransactionMaster> findByMerchantIdAndStatusEquals(String merchantId, String status);

        // Find transactions with pagination
        Page<TransactionMaster> findByMerchantId(String merchantId, Pageable pageable);

        Page<TransactionMaster> findByMerchantIdAndTxnDateBetweenAndStatusEquals(String merchantId,
                        java.sql.Date startDate,
                        java.sql.Date endDate, String status, Pageable pageable);

        Page<TransactionMaster> findByMerchantIdAndTxnDateBetween(String merchantId, java.sql.Date startDate,
                        java.sql.Date endDate, Pageable pageable);

        Page<TransactionMaster> findByMerchantIdAndStatusEquals(String merchantId, String status, Pageable pageable);

        // Aggregation methods for summary
        @Query("SELECT COUNT(*) FROM operators.transaction_master WHERE merchant_id = :merchantId")
        Long countByMerchantId(String merchantId);

        @Query("SELECT COUNT(*) FROM operators.transaction_master WHERE merchant_id = :merchantId AND status = :status")
        Long countByMerchantIdAndStatus(String merchantId, String status);

        @Query("SELECT COALESCE(SUM(amount), 0) FROM operators.transaction_master WHERE merchant_id = :merchantId AND status='completed'")
        BigDecimal sumAmountByMerchantId(String merchantId);

        @Query("SELECT DISTINCT status FROM operators.transaction_master WHERE merchant_id = :merchantId")
        List<String> findDistinctStatusByMerchantId(String merchantId);

        @Query(value = "SELECT DATE(txn_date) as date, COUNT(*) as count, COALESCE(SUM(amount), 0) as total " +
                        "FROM operators.transaction_master " +
                        "WHERE txn_date BETWEEN :startDate AND :endDate " +
                        "GROUP BY DATE(txn_date) " +
                        "ORDER BY date", nativeQuery = true)
        List<DailyVolumeProjection> getDailyVolume(java.sql.Date startDate, java.sql.Date endDate);

        @Query(value = "SELECT " +
                        "  DATE_TRUNC('week', txn_date)::date as week_start, " +
                        "  EXTRACT(WEEK FROM txn_date)::integer as week_number, " +
                        "  COUNT(*) as count, " +
                        "  COALESCE(SUM(amount), 0) as total " +
                        "FROM operators.transaction_master " +
                        "WHERE txn_date BETWEEN :startDate AND :endDate " +
                        "GROUP BY week_start, week_number " +
                        "ORDER BY week_start", nativeQuery = true)
        List<WeeklyVolumeProjection> getWeeklyVolume(java.sql.Date startDate, java.sql.Date endDate);

        @Query(value = "SELECT " +
                        "  TO_CHAR(txn_date, 'YYYY-MM') as month, " +
                        "  COUNT(*) as count, " +
                        "  COALESCE(SUM(amount), 0) as total " +
                        "FROM operators.transaction_master " +
                        "WHERE txn_date BETWEEN :startDate AND :endDate " +
                        "GROUP BY month " +
                        "ORDER BY month", nativeQuery = true)
        List<MonthlyVolumeProjection> getMonthlyVolume(java.sql.Date startDate, java.sql.Date endDate);

        @Query("SELECT COUNT(*) FROM operators.transaction_master WHERE txn_date BETWEEN :startDate AND :endDate")
        Long getTotalTransactionCount(java.sql.Date startDate, java.sql.Date endDate);

        @Query(value = "SELECT COALESCE(status, 'unknown') as status, COUNT(*) as count " +
                        "FROM operators.transaction_master " +
                        "WHERE txn_date BETWEEN :startDate AND :endDate " +
                        "GROUP BY status", nativeQuery = true)
        List<StatusCountProjection> getTransactionsByStatus(java.sql.Date startDate, java.sql.Date endDate);

        @Query(value = "SELECT " +
                        "  COALESCE(AVG(amount), 0) as avg_amount, " +
                        "  COALESCE(PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY amount), 0) as median_amount, " +
                        "  COALESCE(MIN(amount), 0) as min_amount, " +
                        "  COALESCE(MAX(amount), 0) as max_amount " +
                        "FROM operators.transaction_master " +
                        "WHERE txn_date BETWEEN :startDate AND :endDate", nativeQuery = true)
        AmountStatsProjection getAmountStatistics(java.sql.Date startDate, java.sql.Date endDate);

        @Query(value = "SELECT " +
                        "  DATE(txn_date) as date, " +
                        "  COALESCE(AVG(amount), 0) as avg_amount " +
                        "FROM operators.transaction_master " +
                        "WHERE txn_date BETWEEN :startDate AND :endDate " +
                        "GROUP BY DATE(txn_date) " +
                        "ORDER BY date", nativeQuery = true)
        List<DailyAmountProjection> getDailyAverageAmount(java.sql.Date startDate, java.sql.Date endDate);

        @Query(value = "SELECT " +
                        "  EXTRACT(HOUR FROM local_txn_date_time)::integer as hour, " +
                        "  COUNT(*) as count " +
                        "FROM operators.transaction_master " +
                        "WHERE txn_date BETWEEN :startDate AND :endDate " +
                        "  AND local_txn_date_time IS NOT NULL " +
                        "GROUP BY hour " +
                        "ORDER BY hour", nativeQuery = true)
        List<HourlyCountProjection> getHourlyDistribution(java.sql.Date startDate, java.sql.Date endDate);

        @Query(value = "SELECT " +
                        "  EXTRACT(DOW FROM txn_date)::integer as dow, " +
                        "  COUNT(*) as count " +
                        "FROM operators.transaction_master " +
                        "WHERE txn_date BETWEEN :startDate AND :endDate " +
                        "GROUP BY dow " +
                        "ORDER BY dow", nativeQuery = true)
        List<DayOfWeekCountProjection> getDayOfWeekDistribution(java.sql.Date startDate, java.sql.Date endDate);

        @Query(value = "SELECT " +
                        "  COALESCE(LOWER(card_type), 'unknown') as card_type, " +
                        "  COUNT(*) as count " +
                        "FROM operators.transaction_master " +
                        "WHERE txn_date BETWEEN :startDate AND :endDate " +
                        "GROUP BY card_type " +
                        "ORDER BY count DESC", nativeQuery = true)
        List<CardTypeCountProjection> getCardTypeDistribution(java.sql.Date startDate, java.sql.Date endDate);
}
