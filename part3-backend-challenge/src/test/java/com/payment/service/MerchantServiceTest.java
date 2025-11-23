package com.payment.service;

import com.payment.dto.MerchantListResponse;
import com.payment.dto.MerchantSearchRequest;
import com.payment.entity.Merchant;
import com.payment.repository.MerchantRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MerchantServiceTest {

        @Mock
        private MerchantRepository merchantRepository;

        @InjectMocks
        private MerchantService merchantService;

        private Merchant testMerchant1;
        private Merchant testMerchant2;
        private Page<Merchant> mockPage;

        @BeforeEach
        void setUp() {
                testMerchant1 = Merchant.builder()
                                .merchantId("MCH-00001")
                                .merchantName("TechHub Electronics")
                                .businessName("TechHub Electronics LLC")
                                .email("contact@techhub.com")
                                .phone("+1-555-0101")
                                .businessType("retail")
                                .registrationNumber("REG-2024-001")
                                .isActive(true)
                                .createdAt(Instant.now())
                                .updatedAt(Instant.now())
                                .build();

                testMerchant2 = Merchant.builder()
                                .merchantId("MCH-00002")
                                .merchantName("Fashion Forward")
                                .businessName("Fashion Forward Inc")
                                .email("info@fashionforward.com")
                                .phone("+1-555-0102")
                                .businessType("retail")
                                .registrationNumber("REG-2024-002")
                                .isActive(true)
                                .createdAt(Instant.now())
                                .updatedAt(Instant.now())
                                .build();
        }

        @Test
        void testGetActiveMerchantsWithoutFilters() {
                // Arrange
                List<Merchant> merchants = Arrays.asList(testMerchant1, testMerchant2);
                mockPage = Page.of(merchants, Pageable.from(0, 20), 2);

                when(merchantRepository.findByIsActive(eq(true), any(Pageable.class))).thenReturn(mockPage);

                MerchantSearchRequest request = MerchantSearchRequest.builder()
                                .page(0)
                                .size(20)
                                .sortBy("merchantId")
                                .sortDirection("ASC")
                                .build();

                // Act
                MerchantListResponse response = merchantService.getMerchants(request);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getMerchants()).hasSize(2);
                assertThat(response.getPagination().getTotalElements()).isEqualTo(2);
                assertThat(response.getPagination().getTotalPages()).isEqualTo(1);

                verify(merchantRepository, times(1)).findByIsActive(eq(true), any(Pageable.class));
        }

        @Test
        void testGetMerchantsFilteredByName() {
                // Arrange
                List<Merchant> merchants = Collections.singletonList(testMerchant1);
                mockPage = Page.of(merchants, Pageable.from(0, 20), 1);

                when(merchantRepository.findByMerchantNameContainsIgnoreCaseAndIsActive(
                                eq("TechHub"), eq(true), any(Pageable.class))).thenReturn(mockPage);

                MerchantSearchRequest request = MerchantSearchRequest.builder()
                                .merchantName("TechHub")
                                .page(0)
                                .size(20)
                                .sortBy("merchantId")
                                .sortDirection("ASC")
                                .build();

                // Act
                MerchantListResponse response = merchantService.getMerchants(request);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getMerchants()).hasSize(1);
                assertThat(response.getMerchants().get(0).getMerchantName()).isEqualTo("TechHub Electronics");

                verify(merchantRepository, times(1))
                                .findByMerchantNameContainsIgnoreCaseAndIsActive(eq("TechHub"), eq(true),
                                                any(Pageable.class));
        }

        @Test
        void testGetMerchantsFilteredById() {
                // Arrange
                List<Merchant> merchants = Collections.singletonList(testMerchant1);
                mockPage = Page.of(merchants, Pageable.from(0, 20), 1);

                when(merchantRepository.findByMerchantIdAndIsActive(eq("MCH-00001"), eq(true), any(Pageable.class)))
                                .thenReturn(mockPage);

                MerchantSearchRequest request = MerchantSearchRequest.builder()
                                .merchantId("MCH-00001")
                                .page(0)
                                .size(20)
                                .sortBy("merchantId")
                                .sortDirection("ASC")
                                .build();

                // Act
                MerchantListResponse response = merchantService.getMerchants(request);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getMerchants()).hasSize(1);
                assertThat(response.getMerchants().get(0).getMerchantId()).isEqualTo("MCH-00001");

                verify(merchantRepository, times(1))
                                .findByMerchantIdAndIsActive(eq("MCH-00001"), eq(true), any(Pageable.class));
        }

        @Test
        void testGetMerchantsEmptyResult() {
                // Arrange
                mockPage = Page.of(Collections.emptyList(), Pageable.from(0, 20), 0);

                when(merchantRepository.findByIsActive(eq(true), any(Pageable.class))).thenReturn(mockPage);

                MerchantSearchRequest request = MerchantSearchRequest.builder()
                                .page(0)
                                .size(20)
                                .sortBy("merchantId")
                                .sortDirection("ASC")
                                .build();

                // Act
                MerchantListResponse response = merchantService.getMerchants(request);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getMerchants()).isEmpty();
                assertThat(response.getPagination().getTotalElements()).isEqualTo(0);
        }

        @Test
        void testGetMerchantsWithMultiSort() {
                // Arrange
                List<Merchant> merchants = Arrays.asList(testMerchant2, testMerchant1);
                mockPage = Page.of(merchants, Pageable.from(0, 20), 2);

                when(merchantRepository.findByIsActive(eq(true), any(Pageable.class))).thenReturn(mockPage);

                MerchantSearchRequest request = MerchantSearchRequest.builder()
                                .page(0)
                                .size(20)
                                .sortBy("businessType,merchantName")
                                .sortDirection("ASC,DESC")
                                .build();

                // Act
                MerchantListResponse response = merchantService.getMerchants(request);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getMerchants()).hasSize(2);

                verify(merchantRepository, times(1)).findByIsActive(eq(true), any(Pageable.class));
        }

        @Test
        void testGetMerchantsWithPagination() {
                // Arrange
                List<Merchant> merchants = Collections.singletonList(testMerchant1);
                mockPage = Page.of(merchants, Pageable.from(1, 10), 25);

                when(merchantRepository.findByIsActive(eq(true), any(Pageable.class))).thenReturn(mockPage);

                MerchantSearchRequest request = MerchantSearchRequest.builder()
                                .page(1)
                                .size(10)
                                .sortBy("merchantId")
                                .sortDirection("ASC")
                                .build();

                // Act
                MerchantListResponse response = merchantService.getMerchants(request);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getPagination().getPage()).isEqualTo(1);
                assertThat(response.getPagination().getSize()).isEqualTo(10);
                assertThat(response.getPagination().getTotalPages()).isEqualTo(3);
                assertThat(response.getPagination().getTotalElements()).isEqualTo(25);
        }
}
