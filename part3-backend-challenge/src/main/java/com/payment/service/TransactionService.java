package com.payment.service;

import com.payment.dto.common.*;
import com.payment.dto.reports.*;
import com.payment.dto.transaction.*;
import com.payment.entity.TransactionDetail;
import com.payment.entity.TransactionMaster;
import com.payment.repository.TransactionDetailRepository;
import com.payment.repository.TransactionRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        public MerchantTransactionResponse getMerchantTransactions(String merchantId, TransactionRequest request) {
                LOG.info("Fetching transactions for merchant: {}", merchantId);

                // Prepare date range
                Date startDate = request.getStartDate() != null ? Date.valueOf(request.getStartDate()) : null;
                Date endDate = request.getEndDate() != null ? Date.valueOf(request.getEndDate()) : null;

                // Prepare page parameters with default sorting by txnDate DESC (latest first)
                int page = request.getPage() != null ? request.getPage() : 0;
                int size = request.getSize() != null ? request.getSize() : 20;
                Pageable pageable = Pageable.from(page, size, io.micronaut.data.model.Sort.of(
                                io.micronaut.data.model.Sort.Order.desc("txnDate")));

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
                TransactionSummaryDTO summary = calculateSummary(merchantId);

                // Convert transactions to response DTOs
                List<TransactionResponse> transactionResponses = transactionPage.getContent().stream()
                                .map(this::convertToTransactionResponse)
                                .collect(Collectors.toList());

                // Prepare date range for response
                Instant startInstant = startDate != null
                                ? startDate.toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant()
                                : null;
                Instant endInstant = endDate != null
                                ? endDate.toLocalDate().atStartOfDay(ZoneOffset.UTC).plusDays(1).minusNanos(1)
                                                .toInstant()
                                : null;

                if (startDate != null && endDate != null) {
                        // If both dates are provided, set end to end of day (23:59:59)
                        endInstant = endDate.toLocalDate().atStartOfDay(ZoneOffset.UTC).plusDays(1).minusNanos(1)
                                        .toInstant();
                } else if (startDate != null) {
                        // If only start date is provided, set end to end of today
                        endInstant = LocalDate.now().atStartOfDay().plusDays(1).minusNanos(1).atZone(ZoneOffset.UTC)
                                        .toInstant();
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

        private TransactionSummaryDTO calculateSummary(String merchantId) {
                Long totalTransactions;
                BigDecimal totalAmount;
                List<String> distinctStatuses;

                // Always calculate based on merchantId only (ignore date/status filters)
                totalTransactions = transactionRepository.countByMerchantId(merchantId);
                totalAmount = transactionRepository.sumAmountByMerchantId(merchantId);
                distinctStatuses = transactionRepository.findDistinctStatusByMerchantId(merchantId);

                // Count transactions by status
                Map<String, Long> byStatus = distinctStatuses.stream()
                                .collect(Collectors.toMap(
                                                Function.identity(),
                                                statusValue -> {

                                                        return transactionRepository.countByMerchantIdAndStatus(
                                                                        merchantId, statusValue);

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
                                .txnDate(transaction.getTxnDate())
                                .timestamp(timestamp)
                                .cardType(transaction.getCardType())
                                .cardLast4(transaction.getCardLast4())
                                .acquirer("Global Payment Services") // Placeholder - could be retrieved from another
                                                                     // table
                                .issuer("Visa Worldwide") // Placeholder - could be retrieved from another table
                                .details(detailDTOs)
                                .build();
        }

        public TransactionReportsResponse getTransactionReports(LocalDate startDate, LocalDate endDate) {
                LOG.info("Generating transaction reports for period: {} to {}", startDate, endDate);

                // Default to last 30 days if not specified
                if (endDate == null) {
                        endDate = LocalDate.now();
                }
                if (startDate == null) {
                        startDate = endDate.minusDays(30);
                }

                Date sqlStartDate = Date.valueOf(startDate);
                Date sqlEndDate = Date.valueOf(endDate);

                // Build response
                return TransactionReportsResponse.builder()
                                .reportPeriod(buildReportPeriod(startDate, endDate))
                                .volumeMetrics(buildVolumeMetrics(sqlStartDate, sqlEndDate))
                                .successRateMetrics(buildSuccessRateMetrics(sqlStartDate, sqlEndDate))
                                .amountTrends(buildAmountTrends(sqlStartDate, sqlEndDate))
                                .peakTimesHeatmap(buildPeakTimesHeatmap(sqlStartDate, sqlEndDate))
                                .cardTypeDistribution(buildCardTypeDistribution(sqlStartDate, sqlEndDate))
                                .build();
        }

        private ReportPeriodDTO buildReportPeriod(LocalDate startDate, LocalDate endDate) {
                return ReportPeriodDTO.builder()
                                .start(startDate.atStartOfDay(ZoneOffset.UTC).toInstant())
                                .end(endDate.atTime(23, 59, 59).atZone(ZoneOffset.UTC).toInstant())
                                .build();
        }

        private VolumeMetricsDTO buildVolumeMetrics(Date startDate, Date endDate) {
                // Daily volume
                List<com.payment.repository.projection.DailyVolumeProjection> dailyResults = transactionRepository
                                .getDailyVolume(startDate, endDate);
                LOG.debug("Daily volume query returned {} results", dailyResults != null ? dailyResults.size() : 0);

                List<DailyVolumeDTO> daily = dailyResults.stream()
                                .map(proj -> DailyVolumeDTO.builder()
                                                .date(proj.getDate() != null ? proj.getDate() : "")
                                                .count(proj.getCount() != null ? proj.getCount() : 0L)
                                                .amount(proj.getTotal() != null ? proj.getTotal() : BigDecimal.ZERO)
                                                .build())
                                .collect(Collectors.toList());

                List<com.payment.repository.projection.WeeklyVolumeProjection> weeklyResults = transactionRepository
                                .getWeeklyVolume(startDate, endDate);
                List<WeeklyVolumeDTO> weekly = weeklyResults.stream()
                                .map(proj -> WeeklyVolumeDTO.builder()
                                                .weekStart(proj.getWeekStart() != null ? proj.getWeekStart() : "")
                                                .weekNumber(proj.getWeekNumber() != null ? proj.getWeekNumber() : 0)
                                                .count(proj.getCount() != null ? proj.getCount() : 0L)
                                                .amount(proj.getTotal() != null ? proj.getTotal() : BigDecimal.ZERO)
                                                .build())
                                .collect(Collectors.toList());

                List<com.payment.repository.projection.MonthlyVolumeProjection> monthlyResults = transactionRepository
                                .getMonthlyVolume(startDate, endDate);
                List<MonthlyVolumeDTO> monthly = monthlyResults.stream()
                                .map(proj -> MonthlyVolumeDTO.builder()
                                                .month(proj.getMonth() != null ? proj.getMonth() : "")
                                                .count(proj.getCount() != null ? proj.getCount() : 0L)
                                                .amount(proj.getTotal() != null ? proj.getTotal() : BigDecimal.ZERO)
                                                .build())
                                .collect(Collectors.toList());
                return VolumeMetricsDTO.builder()
                                .daily(daily)
                                .weekly(weekly)
                                .monthly(monthly)
                                .build();
        }

        private SuccessRateMetricsDTO buildSuccessRateMetrics(Date startDate, Date endDate) {
                Long totalTransactions = transactionRepository.getTotalTransactionCount(startDate, endDate);
                if (totalTransactions == null || totalTransactions == 0) {
                        totalTransactions = 0L;
                }

                List<com.payment.repository.projection.StatusCountProjection> statusResults = transactionRepository
                                .getTransactionsByStatus(startDate, endDate);
                Map<String, Long> byStatus = statusResults.stream()
                                .collect(Collectors.toMap(
                                                proj -> proj.getStatus() != null ? proj.getStatus() : "unknown",
                                                proj -> proj.getCount() != null ? proj.getCount() : 0L));

                Long completed = byStatus.getOrDefault("completed", 0L);
                Long failed = byStatus.getOrDefault("failed", 0L);

                Double successRate = totalTransactions > 0 ? (completed.doubleValue() / totalTransactions) * 100 : 0.0;
                Double failureRate = totalTransactions > 0 ? (failed.doubleValue() / totalTransactions) * 100 : 0.0;

                return SuccessRateMetricsDTO.builder()
                                .totalTransactions(totalTransactions)
                                .completed(completed)
                                .failed(failed)
                                .successRate(Math.round(successRate * 100.0) / 100.0) // Round to 2 decimal places
                                .failureRate(Math.round(failureRate * 100.0) / 100.0)
                                .byStatus(byStatus)
                                .build();
        }

        private AmountTrendsDTO buildAmountTrends(Date startDate, Date endDate) {
                com.payment.repository.projection.AmountStatsProjection stats = transactionRepository
                                .getAmountStatistics(startDate, endDate);
                AmountStatsDTO overallStats = null;
                if (stats != null) {
                        overallStats = AmountStatsDTO.builder()
                                        .average(stats.getAvgAmount() != null
                                                        ? stats.getAvgAmount().setScale(2,
                                                                        java.math.RoundingMode.HALF_UP)
                                                        : BigDecimal.ZERO)
                                        .median(stats.getMedianAmount() != null
                                                        ? stats.getMedianAmount().setScale(2,
                                                                        java.math.RoundingMode.HALF_UP)
                                                        : BigDecimal.ZERO)
                                        .min(stats.getMinAmount() != null
                                                        ? stats.getMinAmount().setScale(2,
                                                                        java.math.RoundingMode.HALF_UP)
                                                        : BigDecimal.ZERO)
                                        .max(stats.getMaxAmount() != null
                                                        ? stats.getMaxAmount().setScale(2,
                                                                        java.math.RoundingMode.HALF_UP)
                                                        : BigDecimal.ZERO)
                                        .build();
                }

                List<com.payment.repository.projection.DailyAmountProjection> dailyResults = transactionRepository
                                .getDailyAverageAmount(startDate, endDate);
                List<DailyAmountTrendDTO> dailyTrends = dailyResults.stream()
                                .map(proj -> DailyAmountTrendDTO.builder()
                                                .date(proj.getDate() != null ? proj.getDate() : "")
                                                .average(proj.getAvgAmount() != null
                                                                ? proj.getAvgAmount().setScale(2,
                                                                                java.math.RoundingMode.HALF_UP)
                                                                : BigDecimal.ZERO)
                                                .build())
                                .collect(Collectors.toList());

                return AmountTrendsDTO.builder()
                                .overall(overallStats)
                                .daily(dailyTrends)
                                .build();
        }

        private PeakTimesHeatmapDTO buildPeakTimesHeatmap(Date startDate, Date endDate) {
                List<com.payment.repository.projection.HourlyCountProjection> hourlyResults = transactionRepository
                                .getHourlyDistribution(startDate, endDate);
                List<HourlyDistributionDTO> hourly = hourlyResults.stream()
                                .map(proj -> HourlyDistributionDTO.builder()
                                                .hour(proj.getHour() != null ? proj.getHour() : 0)
                                                .count(proj.getCount() != null ? proj.getCount() : 0L)
                                                .build())
                                .collect(Collectors.toList());

                String[] dayNames = { "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY" };
                List<com.payment.repository.projection.DayOfWeekCountProjection> dowResults = transactionRepository
                                .getDayOfWeekDistribution(startDate, endDate);
                List<DayOfWeekDistributionDTO> dayOfWeek = dowResults.stream()
                                .map(proj -> {
                                        int dow = proj.getDow() != null ? proj.getDow() : 0;
                                        return DayOfWeekDistributionDTO.builder()
                                                        .dayOfWeek(dayNames[dow])
                                                        .count(proj.getCount() != null ? proj.getCount() : 0L)
                                                        .build();
                                })
                                .collect(Collectors.toList());

                return PeakTimesHeatmapDTO.builder()
                                .hourly(hourly)
                                .dayOfWeek(dayOfWeek)
                                .build();
        }

        private CardTypeDistributionDTO buildCardTypeDistribution(Date startDate, Date endDate) {
                List<com.payment.repository.projection.CardTypeCountProjection> results = transactionRepository
                                .getCardTypeDistribution(startDate, endDate);

                long total = results.stream()
                                .mapToLong(proj -> proj.getCount() != null ? proj.getCount() : 0L)
                                .sum();

                Map<String, Long> byType = results.stream()
                                .collect(Collectors.toMap(
                                                proj -> proj.getCardType() != null ? proj.getCardType() : "unknown",
                                                proj -> proj.getCount() != null ? proj.getCount() : 0L));

                Map<String, Double> percentages = results.stream()
                                .collect(Collectors.toMap(
                                                proj -> proj.getCardType() != null ? proj.getCardType() : "unknown",
                                                proj -> {
                                                        long count = proj.getCount() != null ? proj.getCount() : 0L;
                                                        double percentage = total > 0 ? (count * 100.0) / total : 0.0;
                                                        return Math.round(percentage * 100.0) / 100.0; // Round to 2
                                                                                                       // decimal places
                                                }));

                return CardTypeDistributionDTO.builder()
                                .byType(byType)
                                .percentages(percentages)
                                .build();
        }
}
