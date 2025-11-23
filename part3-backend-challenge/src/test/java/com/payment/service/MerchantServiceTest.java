package com.payment.service;

import com.payment.dto.CreateMerchantRequest;
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
        void testGetAllMerchantsWithoutFilters() {
                // Arrange
                List<Merchant> merchants = Arrays.asList(testMerchant1, testMerchant2);
                mockPage = Page.of(merchants, Pageable.from(0, 20), 2);

                when(merchantRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

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

                verify(merchantRepository, times(1)).findAll(any(Pageable.class));
        }

        @Test
        void testGetMerchantsFilteredByName() {
                // Arrange
                List<Merchant> merchants = Collections.singletonList(testMerchant1);
                mockPage = Page.of(merchants, Pageable.from(0, 20), 1);

                when(merchantRepository.findByMerchantNameContainsIgnoreCase(
                                eq("TechHub"), any(Pageable.class))).thenReturn(mockPage);

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
                                .findByMerchantNameContainsIgnoreCase(eq("TechHub"),
                                                any(Pageable.class));
        }

        @Test
        void testGetMerchantsFilteredById() {
                // Arrange
                List<Merchant> merchants = Collections.singletonList(testMerchant1);
                mockPage = Page.of(merchants, Pageable.from(0, 20), 1);

                when(merchantRepository.findByMerchantId(eq("MCH-00001"), any(Pageable.class)))
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
                                .findByMerchantId(eq("MCH-00001"), any(Pageable.class));
        }

        @Test
        void testGetMerchantsEmptyResult() {
                // Arrange
                mockPage = Page.of(Collections.emptyList(), Pageable.from(0, 20), 0);

                when(merchantRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

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

                when(merchantRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

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

                verify(merchantRepository, times(1)).findAll(any(Pageable.class));
        }

        @Test
        void testGetMerchantsWithPagination() {
                // Arrange
                List<Merchant> merchants = Collections.singletonList(testMerchant1);
                mockPage = Page.of(merchants, Pageable.from(1, 10), 25);

                when(merchantRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

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

        @Test
        void testGetMerchantByIdSuccess() {
                // Arrange
                when(merchantRepository.findById("MCH-00001")).thenReturn(java.util.Optional.of(testMerchant1));

                // Act
                com.payment.dto.MerchantResponse response = merchantService.getMerchantById("MCH-00001");

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getMerchantId()).isEqualTo("MCH-00001");
                assertThat(response.getMerchantName()).isEqualTo("TechHub Electronics");
                assertThat(response.getBusinessName()).isEqualTo("TechHub Electronics LLC");
                assertThat(response.getEmail()).isEqualTo("contact@techhub.com");

                verify(merchantRepository, times(1)).findById("MCH-00001");
        }

        @Test
        void testGetMerchantByIdNotFound() {
                // Arrange
                when(merchantRepository.findById("MCH-99999")).thenReturn(java.util.Optional.empty());

                // Act & Assert
                org.junit.jupiter.api.Assertions.assertThrows(
                                com.payment.exception.NotFoundException.class,
                                () -> merchantService.getMerchantById("MCH-99999"));

                verify(merchantRepository, times(1)).findById("MCH-99999");
        }

        @Test
        void testGetMerchantByIdInactive() {
                // Arrange
                Merchant inactiveMerchant = Merchant.builder()
                                .merchantId("MCH-00003")
                                .merchantName("Inactive Store")
                                .businessName("Inactive Store Inc")
                                .email("info@inactive.com")
                                .phone("+1-555-0103")
                                .businessType("retail")
                                .registrationNumber("REG-2024-003")
                                .isActive(false)
                                .createdAt(Instant.now())
                                .updatedAt(Instant.now())
                                .build();

                when(merchantRepository.findById("MCH-00003")).thenReturn(java.util.Optional.of(inactiveMerchant));

                // Act
                com.payment.dto.MerchantResponse response = merchantService.getMerchantById("MCH-00003");

                // Assert - inactive merchants are now returned
                assertThat(response).isNotNull();
                assertThat(response.getMerchantId()).isEqualTo("MCH-00003");
                assertThat(response.getIsActive()).isFalse();

                verify(merchantRepository, times(1)).findById("MCH-00003");
        }

        @Test
        void testGetMerchantByIdInvalidFormat() {
                // Act & Assert - Invalid format
                org.junit.jupiter.api.Assertions.assertThrows(
                                IllegalArgumentException.class,
                                () -> merchantService.getMerchantById("INVALID-ID"));

                // Verify repository was never called
                verify(merchantRepository, never()).findById(anyString());
        }

        @Test
        void testGetMerchantByIdNullId() {
                // Act & Assert - Null ID
                org.junit.jupiter.api.Assertions.assertThrows(
                                IllegalArgumentException.class,
                                () -> merchantService.getMerchantById(null));

                // Verify repository was never called
                verify(merchantRepository, never()).findById(anyString());
        }

        @Test
        void testGetMerchantByIdEmptyId() {
                // Act & Assert - Empty ID
                org.junit.jupiter.api.Assertions.assertThrows(
                                IllegalArgumentException.class,
                                () -> merchantService.getMerchantById(""));

                // Verify repository was never called
                verify(merchantRepository, never()).findById(anyString());
        }

        // ============ UPDATE MERCHANT TESTS ============

        @Test
        void testUpdateMerchantSuccess() {
                // Arrange - Update all fields
                when(merchantRepository.findById("MCH-00001")).thenReturn(java.util.Optional.of(testMerchant1));
                when(merchantRepository.update(any(Merchant.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                com.payment.dto.UpdateMerchantRequest updateRequest = com.payment.dto.UpdateMerchantRequest.builder()
                                .email("newemail@techhub.com")
                                .phone("+1-555-9999")
                                .isActive(false)
                                .build();

                // Act
                com.payment.dto.MerchantResponse response = merchantService.updateMerchant("MCH-00001", updateRequest);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getEmail()).isEqualTo("newemail@techhub.com");
                assertThat(response.getPhone()).isEqualTo("+1-555-9999");
                assertThat(response.getIsActive()).isFalse();

                verify(merchantRepository, times(1)).findById("MCH-00001");
                verify(merchantRepository, times(1)).update(any(Merchant.class));
        }

        @Test
        void testUpdateMerchantEmailOnly() {
                // Arrange - Update only email
                when(merchantRepository.findById("MCH-00001")).thenReturn(java.util.Optional.of(testMerchant1));
                when(merchantRepository.update(any(Merchant.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                com.payment.dto.UpdateMerchantRequest updateRequest = com.payment.dto.UpdateMerchantRequest.builder()
                                .email("updatedemail@techhub.com")
                                .build();

                // Act
                com.payment.dto.MerchantResponse response = merchantService.updateMerchant("MCH-00001", updateRequest);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getEmail()).isEqualTo("updatedemail@techhub.com");
                assertThat(response.getPhone()).isEqualTo("+1-555-0101"); // Unchanged
                assertThat(response.getIsActive()).isTrue(); // Unchanged

                verify(merchantRepository, times(1)).findById("MCH-00001");
                verify(merchantRepository, times(1)).update(any(Merchant.class));
        }

        @Test
        void testUpdateMerchantPhoneOnly() {
                // Arrange - Update only phone
                when(merchantRepository.findById("MCH-00001")).thenReturn(java.util.Optional.of(testMerchant1));
                when(merchantRepository.update(any(Merchant.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                com.payment.dto.UpdateMerchantRequest updateRequest = com.payment.dto.UpdateMerchantRequest.builder()
                                .phone("+1-555-8888")
                                .build();

                // Act
                com.payment.dto.MerchantResponse response = merchantService.updateMerchant("MCH-00001", updateRequest);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getPhone()).isEqualTo("+1-555-8888");
                assertThat(response.getEmail()).isEqualTo("contact@techhub.com"); // Unchanged
                assertThat(response.getIsActive()).isTrue(); // Unchanged

                verify(merchantRepository, times(1)).findById("MCH-00001");
                verify(merchantRepository, times(1)).update(any(Merchant.class));
        }

        @Test
        void testUpdateMerchantIsActiveOnly() {
                // Arrange - Update only isActive status
                when(merchantRepository.findById("MCH-00001")).thenReturn(java.util.Optional.of(testMerchant1));
                when(merchantRepository.update(any(Merchant.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                com.payment.dto.UpdateMerchantRequest updateRequest = com.payment.dto.UpdateMerchantRequest.builder()
                                .isActive(false)
                                .build();

                // Act
                com.payment.dto.MerchantResponse response = merchantService.updateMerchant("MCH-00001", updateRequest);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getIsActive()).isFalse();
                assertThat(response.getEmail()).isEqualTo("contact@techhub.com"); // Unchanged
                assertThat(response.getPhone()).isEqualTo("+1-555-0101"); // Unchanged

                verify(merchantRepository, times(1)).findById("MCH-00001");
                verify(merchantRepository, times(1)).update(any(Merchant.class));
        }

        @Test
        void testUpdateMerchantNotFound() {
                // Arrange - Merchant doesn't exist
                when(merchantRepository.findById("MCH-99999")).thenReturn(java.util.Optional.empty());

                com.payment.dto.UpdateMerchantRequest updateRequest = com.payment.dto.UpdateMerchantRequest.builder()
                                .email("test@example.com")
                                .build();

                // Act & Assert
                org.junit.jupiter.api.Assertions.assertThrows(
                                com.payment.exception.NotFoundException.class,
                                () -> merchantService.updateMerchant("MCH-99999", updateRequest));

                verify(merchantRepository, times(1)).findById("MCH-99999");
                verify(merchantRepository, never()).update(any(Merchant.class));
        }

        @Test
        void testUpdateMerchantInvalidId() {
                // Arrange - Invalid merchant ID format
                com.payment.dto.UpdateMerchantRequest updateRequest = com.payment.dto.UpdateMerchantRequest.builder()
                                .email("test@example.com")
                                .build();

                // Act & Assert - Invalid format
                org.junit.jupiter.api.Assertions.assertThrows(
                                IllegalArgumentException.class,
                                () -> merchantService.updateMerchant("INVALID-ID", updateRequest));

                // Verify repository was never called
                verify(merchantRepository, never()).findById(anyString());
                verify(merchantRepository, never()).update(any(Merchant.class));
        }

        @Test
        void testUpdateMerchantNullId() {
                // Arrange - Null ID
                com.payment.dto.UpdateMerchantRequest updateRequest = com.payment.dto.UpdateMerchantRequest.builder()
                                .email("test@example.com")
                                .build();

                // Act & Assert - Null ID
                org.junit.jupiter.api.Assertions.assertThrows(
                                IllegalArgumentException.class,
                                () -> merchantService.updateMerchant(null, updateRequest));

                // Verify repository was never called
                verify(merchantRepository, never()).findById(anyString());
                verify(merchantRepository, never()).update(any(Merchant.class));
        }

        @Test
        void testUpdateMerchantNoFieldsProvided() {
                // Arrange - No fields provided for update
                com.payment.dto.UpdateMerchantRequest updateRequest = com.payment.dto.UpdateMerchantRequest.builder()
                                .build();

                // Act & Assert
                org.junit.jupiter.api.Assertions.assertThrows(
                                IllegalArgumentException.class,
                                () -> merchantService.updateMerchant("MCH-00001", updateRequest));

                // Verify repository was never called
                verify(merchantRepository, never()).findById(anyString());
                verify(merchantRepository, never()).update(any(Merchant.class));
        }

        // ===== CREATE MERCHANT TESTS =====

        @Test
        void testCreateMerchantSuccess() {
                // Arrange
                CreateMerchantRequest request = CreateMerchantRequest.builder()
                                .merchantName("TechHub Electronics")
                                .businessName("TechHub Electronics LLC")
                                .email("contact@techhub.com")
                                .phone("9818576955")
                                .businessType("retail")
                                .taxId("123456789")
                                .registrationNumber("REG-2024-001")
                                .build();

                Merchant existingMerchant = Merchant.builder()
                                .merchantId("MCH-00001")
                                .build();

                Merchant savedMerchant = Merchant.builder()
                                .merchantId("MCH-00002")
                                .merchantName("TechHub Electronics")
                                .businessName("TechHub Electronics LLC")
                                .email("contact@techhub.com")
                                .phone("9818576955")
                                .businessType("retail")
                                .taxId("123456789")
                                .registrationNumber("REG-2024-001")
                                .isActive(true)
                                .createdAt(Instant.now())
                                .updatedAt(Instant.now())
                                .build();

                when(merchantRepository.countByEmail("contact@techhub.com")).thenReturn(0L);
                when(merchantRepository.countByTaxId("123456789")).thenReturn(0L);
                when(merchantRepository.countByRegistrationNumber("REG-2024-001")).thenReturn(0L);
                when(merchantRepository.findLatestMerchant()).thenReturn(java.util.Optional.of(existingMerchant));
                when(merchantRepository.save(any(Merchant.class))).thenReturn(savedMerchant);

                // Act
                com.payment.dto.MerchantResponse response = merchantService.createMerchant(request);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getMerchantId()).isEqualTo("MCH-00002");
                assertThat(response.getMerchantName()).isEqualTo("TechHub Electronics");
                assertThat(response.getEmail()).isEqualTo("contact@techhub.com");
                assertThat(response.getBusinessType()).isEqualTo("retail");
                assertThat(response.getIsActive()).isTrue();

                verify(merchantRepository, times(1)).countByEmail("contact@techhub.com");
                verify(merchantRepository, times(1)).findLatestMerchant();
                verify(merchantRepository, times(1)).save(any(Merchant.class));
        }

        @Test
        void testCreateMerchantFirstMerchant() {
                // Arrange - No existing merchants
                CreateMerchantRequest request = CreateMerchantRequest.builder()
                                .merchantName("First Merchant")
                                .businessName("First Business LLC")
                                .email("first@example.com")
                                .phone("1234567890")
                                .businessType("ecommerce")
                                .build();

                Merchant savedMerchant = Merchant.builder()
                                .merchantId("MCH-00001")
                                .merchantName("First Merchant")
                                .businessName("First Business LLC")
                                .email("first@example.com")
                                .phone("1234567890")
                                .businessType("ecommerce")
                                .isActive(true)
                                .createdAt(Instant.now())
                                .updatedAt(Instant.now())
                                .build();

                when(merchantRepository.countByEmail("first@example.com")).thenReturn(0L);
                when(merchantRepository.countByTaxId(any())).thenReturn(0L);
                when(merchantRepository.countByRegistrationNumber(any())).thenReturn(0L);
                when(merchantRepository.findLatestMerchant()).thenReturn(java.util.Optional.empty());
                when(merchantRepository.save(any(Merchant.class))).thenReturn(savedMerchant);

                // Act
                com.payment.dto.MerchantResponse response = merchantService.createMerchant(request);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getMerchantId()).isEqualTo("MCH-00001");
                assertThat(response.getMerchantName()).isEqualTo("First Merchant");

                verify(merchantRepository, times(1)).countByEmail("first@example.com");
                verify(merchantRepository, times(1)).findLatestMerchant();
                verify(merchantRepository, times(1)).save(any(Merchant.class));
        }

        @Test
        void testCreateMerchantDuplicateEmail() {
                // Arrange
                CreateMerchantRequest request = CreateMerchantRequest.builder()
                                .merchantName("Duplicate Merchant")
                                .businessName("Duplicate LLC")
                                .email("existing@example.com")
                                .phone("9999999999")
                                .businessType("retail")
                                .build();

                when(merchantRepository.countByEmail("existing@example.com")).thenReturn(1L);

                // Act & Assert
                org.junit.jupiter.api.Assertions.assertThrows(
                                IllegalArgumentException.class,
                                () -> merchantService.createMerchant(request),
                                "Email already exists: existing@example.com");

                verify(merchantRepository, times(1)).countByEmail("existing@example.com");
                verify(merchantRepository, never()).findLatestMerchant();
                verify(merchantRepository, never()).save(any(Merchant.class));
        }

        @Test
        void testCreateMerchantIdIncrement() {
                // Arrange - Test ID increments correctly
                CreateMerchantRequest request = CreateMerchantRequest.builder()
                                .merchantName("Test Merchant")
                                .businessName("Test LLC")
                                .email("test@example.com")
                                .phone("1111111111")
                                .businessType("services")
                                .build();

                Merchant existingMerchant = Merchant.builder()
                                .merchantId("MCH-00099")
                                .build();

                Merchant savedMerchant = Merchant.builder()
                                .merchantId("MCH-00100")
                                .merchantName("Test Merchant")
                                .businessName("Test LLC")
                                .email("test@example.com")
                                .phone("1111111111")
                                .businessType("services")
                                .isActive(true)
                                .createdAt(Instant.now())
                                .updatedAt(Instant.now())
                                .build();

                when(merchantRepository.countByEmail("test@example.com")).thenReturn(0L);
                when(merchantRepository.countByTaxId(any())).thenReturn(0L);
                when(merchantRepository.countByRegistrationNumber(any())).thenReturn(0L);
                when(merchantRepository.findLatestMerchant()).thenReturn(java.util.Optional.of(existingMerchant));
                when(merchantRepository.save(any(Merchant.class))).thenReturn(savedMerchant);

                // Act
                com.payment.dto.MerchantResponse response = merchantService.createMerchant(request);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getMerchantId()).isEqualTo("MCH-00100");

                verify(merchantRepository, times(1))
                                .save(argThat(merchant -> merchant.getMerchantId().equals("MCH-00100")));
        }

        @Test
        void testCreateMerchantDuplicateTaxId() {
                // Arrange
                CreateMerchantRequest request = CreateMerchantRequest.builder()
                                .merchantName("Duplicate TaxId Merchant")
                                .businessName("Duplicate TaxId LLC")
                                .email("unique@example.com")
                                .phone("8888888888")
                                .businessType("retail")
                                .taxId("TAX-123456")
                                .registrationNumber("REG-UNIQUE-001")
                                .build();

                when(merchantRepository.countByEmail("unique@example.com")).thenReturn(0L);
                when(merchantRepository.countByTaxId("TAX-123456")).thenReturn(1L);

                // Act & Assert
                org.junit.jupiter.api.Assertions.assertThrows(
                                IllegalArgumentException.class,
                                () -> merchantService.createMerchant(request),
                                "Tax ID already exists: TAX-123456");

                verify(merchantRepository, times(1)).countByEmail("unique@example.com");
                verify(merchantRepository, times(1)).countByTaxId("TAX-123456");
                verify(merchantRepository, never()).countByRegistrationNumber(anyString());
                verify(merchantRepository, never()).findLatestMerchant();
                verify(merchantRepository, never()).save(any(Merchant.class));
        }

        @Test
        void testCreateMerchantDuplicateRegistrationNumber() {
                // Arrange
                CreateMerchantRequest request = CreateMerchantRequest.builder()
                                .merchantName("Duplicate RegNum Merchant")
                                .businessName("Duplicate RegNum LLC")
                                .email("unique2@example.com")
                                .phone("7777777777")
                                .businessType("ecommerce")
                                .taxId("TAX-UNIQUE-789")
                                .registrationNumber("REG-123456")
                                .build();

                when(merchantRepository.countByEmail("unique2@example.com")).thenReturn(0L);
                when(merchantRepository.countByTaxId("TAX-UNIQUE-789")).thenReturn(0L);
                when(merchantRepository.countByRegistrationNumber("REG-123456")).thenReturn(1L);

                // Act & Assert
                org.junit.jupiter.api.Assertions.assertThrows(
                                IllegalArgumentException.class,
                                () -> merchantService.createMerchant(request),
                                "Registration number already exists: REG-123456");

                verify(merchantRepository, times(1)).countByEmail("unique2@example.com");
                verify(merchantRepository, times(1)).countByTaxId("TAX-UNIQUE-789");
                verify(merchantRepository, times(1)).countByRegistrationNumber("REG-123456");
                verify(merchantRepository, never()).findLatestMerchant();
                verify(merchantRepository, never()).save(any(Merchant.class));
        }

        @Test
        void testCreateMerchantWithAllRequiredFields() {
                // Arrange - All fields including taxId and registrationNumber are now required
                CreateMerchantRequest request = CreateMerchantRequest.builder()
                                .merchantName("Complete Merchant")
                                .businessName("Complete LLC")
                                .email("complete@example.com")
                                .phone("6666666666")
                                .businessType("services")
                                .taxId("TAX-999888")
                                .registrationNumber("REG-999888")
                                .build();

                Merchant savedMerchant = Merchant.builder()
                                .merchantId("MCH-00001")
                                .merchantName("Complete Merchant")
                                .businessName("Complete LLC")
                                .email("complete@example.com")
                                .phone("6666666666")
                                .businessType("services")
                                .taxId("TAX-999888")
                                .registrationNumber("REG-999888")
                                .isActive(true)
                                .createdAt(Instant.now())
                                .updatedAt(Instant.now())
                                .build();

                when(merchantRepository.countByEmail("complete@example.com")).thenReturn(0L);
                when(merchantRepository.countByTaxId("TAX-999888")).thenReturn(0L);
                when(merchantRepository.countByRegistrationNumber("REG-999888")).thenReturn(0L);
                when(merchantRepository.findLatestMerchant()).thenReturn(java.util.Optional.empty());
                when(merchantRepository.save(any(Merchant.class))).thenReturn(savedMerchant);

                // Act
                com.payment.dto.MerchantResponse response = merchantService.createMerchant(request);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.getMerchantId()).isEqualTo("MCH-00001");
                assertThat(response.getTaxId()).isEqualTo("TAX-999888");
                assertThat(response.getRegistrationNumber()).isEqualTo("REG-999888");

                verify(merchantRepository, times(1)).countByEmail("complete@example.com");
                verify(merchantRepository, times(1)).countByTaxId("TAX-999888");
                verify(merchantRepository, times(1)).countByRegistrationNumber("REG-999888");
                verify(merchantRepository, times(1)).save(any(Merchant.class));
        }
}
