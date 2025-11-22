package com.payment.service;

import com.payment.dto.*;
import com.payment.entity.TransactionMaster;
import com.payment.entity.TransactionDetail;
import com.payment.mapper.TransactionMapper;
import com.payment.repository.TransactionRepository;
import com.payment.repository.TransactionDetailRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionDetailRepository transactionDetailRepository;

    @Mock
    private TransactionMapper transactionMapper;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(transactionRepository, transactionDetailRepository, transactionMapper);
    }

    @Test
    void getMerchantTransactions_WithValidRequest_ReturnsCorrectResponse() {
        // Arrange
        String merchantId = "MCH-00001";
        TransactionRequest request = TransactionRequest.builder()
                .page(0)
                .size(20)
                .startDate(LocalDate.of(2025, 11, 1))
                .endDate(LocalDate.of(2025, 11, 18))
                .status("completed")
                .build();

        TransactionMaster transaction = new TransactionMaster();
        transaction.setTxnId(98765L);
        transaction.setMerchantId(merchantId);
        transaction.setAmount(new BigDecimal("150.00"));
        transaction.setCurrency("USD");
        transaction.setStatus("completed");
        transaction.setCardType("VISA");
        transaction.setCardLast4("4242");
        transaction.setLocalTxnDateTime(Instant.parse("2025-11-18T14:32:15Z"));
        transaction.setCreatedAt(Instant.now());

        TransactionDetail detail = new TransactionDetail();
        detail.setTxnDetailId(123L);
        detail.setMasterTxnId(98765L);
        detail.setDetailType("fee");
        detail.setAmount(new BigDecimal("3.50"));
        detail.setDescription("Processing fee");

        List<TransactionMaster> transactionList = Arrays.asList(transaction);
        Page<TransactionMaster> page = Page.of(transactionList, Pageable.from(0, 20), 1);

        // Mock repository calls
        when(transactionRepository.findByMerchantIdAndTxnDateBetweenAndStatusEquals(
                eq(merchantId), 
                any(Date.class), 
                any(Date.class), 
                eq("completed"), 
                any(Pageable.class)))
                .thenReturn(page);
        
        when(transactionRepository.countByMerchantIdAndTxnDateBetweenAndStatus(
                eq(merchantId), 
                any(Date.class), 
                any(Date.class), 
                eq("completed")))
                .thenReturn(1L);
                
        when(transactionRepository.sumAmountByMerchantIdAndTxnDateBetween(
                eq(merchantId), 
                any(Date.class), 
                any(Date.class)))
                .thenReturn(new BigDecimal("150.00"));
                
        when(transactionRepository.findDistinctStatusByMerchantIdAndTxnDateBetween(
                eq(merchantId), 
                any(Date.class), 
                any(Date.class)))
                .thenReturn(Arrays.asList("completed"));
                
        when(transactionRepository.countByMerchantIdAndTxnDateBetweenAndStatus(
                eq(merchantId), 
                any(Date.class), 
                any(Date.class), 
                eq("completed")))
                .thenReturn(1L);

        when(transactionDetailRepository.findByMasterTxnId(98765L))
                .thenReturn(Arrays.asList(detail));

        // Act
        MerchantTransactionResponse response = transactionService.getMerchantTransactions(merchantId, request);

        // Assert
        assertNotNull(response);
        assertEquals(merchantId, response.getMerchantId());
        assertNotNull(response.getSummary());
        assertNotNull(response.getTransactions());
        assertNotNull(response.getPagination());
        
        assertEquals(1, response.getTransactions().size());
        assertEquals(1, response.getSummary().getTotalTransactions());
        assertEquals(new BigDecimal("150.00"), response.getSummary().getTotalAmount());
        assertEquals("USD", response.getSummary().getCurrency());
        
        TransactionResponse transactionResponse = response.getTransactions().get(0);
        assertEquals(98765L, transactionResponse.getTxnId());
        assertEquals(new BigDecimal("150.00"), transactionResponse.getAmount());
        assertEquals("completed", transactionResponse.getStatus());
        
        assertEquals(1, transactionResponse.getDetails().size());
        assertEquals("fee", transactionResponse.getDetails().get(0).getType());
        
        verify(transactionRepository, times(1)).findByMerchantIdAndTxnDateBetweenAndStatusEquals(
                eq(merchantId), 
                any(Date.class), 
                any(Date.class), 
                eq("completed"), 
                any(Pageable.class));
    }

    @Test
    void getMerchantTransactions_WithNoFilters_ReturnsAllTransactions() {
        // Arrange
        String merchantId = "MCH-00001";
        TransactionRequest request = TransactionRequest.builder()
                .page(0)
                .size(20)
                .build();

        TransactionMaster transaction = new TransactionMaster();
        transaction.setTxnId(98765L);
        transaction.setMerchantId(merchantId);

        List<TransactionMaster> transactionList = Arrays.asList(transaction);
        Page<TransactionMaster> page = Page.of(transactionList, Pageable.from(0, 20), 1);

        // Mock repository calls
        when(transactionRepository.findByMerchantId(merchantId, any(Pageable.class)))
                .thenReturn(page);
        when(transactionRepository.countByMerchantId(merchantId))
                .thenReturn(1L);
        when(transactionRepository.sumAmountByMerchantId(merchantId))
                .thenReturn(new BigDecimal("150.00"));
        when(transactionRepository.findDistinctStatusByMerchantId(merchantId))
                .thenReturn(Arrays.asList("completed"));
        when(transactionRepository.countByMerchantIdAndStatus(eq(merchantId), eq("completed")))
                .thenReturn(1L);

        when(transactionDetailRepository.findByMasterTxnId(98765L))
                .thenReturn(Collections.emptyList());

        // Act
        MerchantTransactionResponse response = transactionService.getMerchantTransactions(merchantId, request);

        // Assert
        assertNotNull(response);
        assertEquals(merchantId, response.getMerchantId());
        assertEquals(1, response.getTransactions().size());
        assertEquals(1, response.getSummary().getTotalTransactions());
        
        verify(transactionRepository, times(1)).findByMerchantId(eq(merchantId), any(Pageable.class));
    }

    @Test
    void getMerchantTransactions_WithEmptyResults_ReturnsEmptyResponse() {
        // Arrange
        String merchantId = "MCH-00001";
        TransactionRequest request = TransactionRequest.builder()
                .page(0)
                .size(20)
                .build();

        List<TransactionMaster> transactionList = Collections.emptyList();
        Page<TransactionMaster> page = Page.of(transactionList, Pageable.from(0, 20), 0);

        // Mock repository calls
        when(transactionRepository.findByMerchantId(merchantId, any(Pageable.class)))
                .thenReturn(page);
        when(transactionRepository.countByMerchantId(merchantId))
                .thenReturn(0L);
        when(transactionRepository.sumAmountByMerchantId(merchantId))
                .thenReturn(null);
        when(transactionRepository.findDistinctStatusByMerchantId(merchantId))
                .thenReturn(Collections.emptyList());

        // Act
        MerchantTransactionResponse response = transactionService.getMerchantTransactions(merchantId, request);

        // Assert
        assertNotNull(response);
        assertEquals(merchantId, response.getMerchantId());
        assertEquals(0, response.getTransactions().size());
        assertEquals(0, response.getSummary().getTotalTransactions());
        assertEquals(BigDecimal.ZERO, response.getSummary().getTotalAmount());
        assertTrue(response.getSummary().getByStatus().isEmpty());
    }

    @Test
    void getMerchantTransactions_WithDateRangeOnly_ReturnsFilteredResults() {
        // Arrange
        String merchantId = "MCH-00001";
        TransactionRequest request = TransactionRequest.builder()
                .page(0)
                .size(20)
                .startDate(LocalDate.of(2025, 11, 1))
                .endDate(LocalDate.of(2025, 11, 18))
                .build();

        TransactionMaster transaction = new TransactionMaster();
        transaction.setTxnId(98765L);
        transaction.setMerchantId(merchantId);

        List<TransactionMaster> transactionList = Arrays.asList(transaction);
        Page<TransactionMaster> page = Page.of(transactionList, Pageable.from(0, 20), 1);

        // Mock repository calls
        when(transactionRepository.findByMerchantIdAndTxnDateBetween(
                eq(merchantId), 
                any(Date.class), 
                any(Date.class), 
                any(Pageable.class)))
                .thenReturn(page);
        when(transactionRepository.countByMerchantIdAndTxnDateBetween(
                eq(merchantId), 
                any(Date.class), 
                any(Date.class)))
                .thenReturn(1L);
        when(transactionRepository.sumAmountByMerchantIdAndTxnDateBetween(
                eq(merchantId), 
                any(Date.class), 
                any(Date.class)))
                .thenReturn(new BigDecimal("150.00"));
        when(transactionRepository.findDistinctStatusByMerchantIdAndTxnDateBetween(
                eq(merchantId), 
                any(Date.class), 
                any(Date.class)))
                .thenReturn(Arrays.asList("completed"));
        when(transactionRepository.countByMerchantIdAndTxnDateBetweenAndStatus(
                eq(merchantId), 
                any(Date.class), 
                any(Date.class), 
                eq("completed")))
                .thenReturn(1L);

        when(transactionDetailRepository.findByMasterTxnId(98765L))
                .thenReturn(Collections.emptyList());

        // Act
        MerchantTransactionResponse response = transactionService.getMerchantTransactions(merchantId, request);

        // Assert
        assertNotNull(response);
        assertEquals(merchantId, response.getMerchantId());
        assertEquals(1, response.getTransactions().size());
        assertEquals(1, response.getSummary().getTotalTransactions());
        
        verify(transactionRepository, times(1)).findByMerchantIdAndTxnDateBetween(
                eq(merchantId), 
                any(Date.class), 
                any(Date.class), 
                any(Pageable.class));
    }

    @Test
    void getMerchantTransactions_WithStatusOnly_ReturnsFilteredResults() {
        // Arrange
        String merchantId = "MCH-00001";
        TransactionRequest request = TransactionRequest.builder()
                .page(0)
                .size(20)
                .status("completed")
                .build();

        TransactionMaster transaction = new TransactionMaster();
        transaction.setTxnId(98765L);
        transaction.setMerchantId(merchantId);

        List<TransactionMaster> transactionList = Arrays.asList(transaction);
        Page<TransactionMaster> page = Page.of(transactionList, Pageable.from(0, 20), 1);

        // Mock repository calls
        when(transactionRepository.findByMerchantIdAndStatusEquals(
                eq(merchantId), 
                eq("completed"), 
                any(Pageable.class)))
                .thenReturn(page);
        when(transactionRepository.countByMerchantIdAndStatus(eq(merchantId), eq("completed")))
                .thenReturn(1L);
        when(transactionRepository.sumAmountByMerchantId(merchantId))
                .thenReturn(new BigDecimal("150.00"));
        when(transactionRepository.findDistinctStatusByMerchantId(merchantId))
                .thenReturn(Arrays.asList("completed"));
        when(transactionRepository.countByMerchantIdAndStatus(eq(merchantId), eq("completed")))
                .thenReturn(1L);

        when(transactionDetailRepository.findByMasterTxnId(98765L))
                .thenReturn(Collections.emptyList());

        // Act
        MerchantTransactionResponse response = transactionService.getMerchantTransactions(merchantId, request);

        // Assert
        assertNotNull(response);
        assertEquals(merchantId, response.getMerchantId());
        assertEquals(1, response.getTransactions().size());
        assertEquals(1, response.getSummary().getTotalTransactions());
        
        verify(transactionRepository, times(1)).findByMerchantIdAndStatusEquals(
                eq(merchantId), 
                eq("completed"), 
                any(Pageable.class));
    }
}
