package com.payment.controller;

import com.payment.dto.ApiResponse;
import com.payment.dto.CreateMerchantRequest;
import com.payment.dto.MerchantResponse;
import com.payment.dto.UpdateMerchantRequest;
import com.payment.exception.NotFoundException;
import com.payment.service.MerchantService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@MicronautTest
class MerchantControllerTest {

        @Inject
        @Client("/")
        HttpClient client;

        @Inject
        MerchantService merchantService;

        private MerchantResponse testMerchantResponse;

        @MockBean(MerchantService.class)
        MerchantService merchantService() {
                return mock(MerchantService.class);
        }

        @BeforeEach
        void setUp() {
                testMerchantResponse = MerchantResponse.builder()
                                .merchantId("MCH-00001")
                                .merchantName("TechHub Electronics")
                                .businessName("TechHub Electronics LLC")
                                .email("contact@techhub.com")
                                .phone("+1-555-0101")
                                .businessType("retail")
                                .registrationNumber("REG-2024-001")
                                .createdAt(Instant.now())
                                .updatedAt(Instant.now())
                                .build();
        }

        @Test
        void testGetMerchantByIdSuccess() {
                // Arrange
                when(merchantService.getMerchantById("MCH-00001")).thenReturn(testMerchantResponse);

                // Act
                HttpRequest<Object> request = HttpRequest.GET("/api/v1/merchants/MCH-00001");
                ApiResponse<MerchantResponse> response = client.toBlocking().retrieve(request, ApiResponse.class);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.isSuccess()).isTrue();
                assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.getCode());
                assertThat(response.getMessage()).isEqualTo("Merchant retrieved successfully");

                verify(merchantService, times(1)).getMerchantById("MCH-00001");
        }

        @Test
        void testGetMerchantByIdNotFound() {
                // Arrange
                when(merchantService.getMerchantById("MCH-99999"))
                                .thenThrow(new NotFoundException("Merchant", "MCH-99999"));

                // Act & Assert
                HttpRequest<Object> request = HttpRequest.GET("/api/v1/merchants/MCH-99999");
                HttpClientResponseException exception = assertThrows(
                                HttpClientResponseException.class,
                                () -> client.toBlocking().retrieve(request, ApiResponse.class));

                assertThat((Object) exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

                verify(merchantService, times(1)).getMerchantById("MCH-99999");
        }

        @Test
        void testGetMerchantByIdInactive() {
                // Arrange
                when(merchantService.getMerchantById("MCH-00003"))
                                .thenThrow(new NotFoundException("Active merchant", "MCH-00003"));

                // Act & Assert
                HttpRequest<Object> request = HttpRequest.GET("/api/v1/merchants/MCH-00003");
                HttpClientResponseException exception = assertThrows(
                                HttpClientResponseException.class,
                                () -> client.toBlocking().retrieve(request, ApiResponse.class));

                assertThat((Object) exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

                verify(merchantService, times(1)).getMerchantById("MCH-00003");
        }

        @Test
        void testGetMerchantByIdInvalidFormat() {
                // Arrange
                when(merchantService.getMerchantById("INVALID-ID"))
                                .thenThrow(new IllegalArgumentException(
                                                "Invalid merchant ID format. Expected format: MCH-XXXXX (e.g., MCH-00001)"));

                // Act & Assert
                HttpRequest<Object> request = HttpRequest.GET("/api/v1/merchants/INVALID-ID");
                HttpClientResponseException exception = assertThrows(
                                HttpClientResponseException.class,
                                () -> client.toBlocking().retrieve(request, ApiResponse.class));

                assertThat((Object) exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

                verify(merchantService, times(1)).getMerchantById("INVALID-ID");
        }

        @Test
        void testGetMerchantByIdNullId() {
                // Arrange
                when(merchantService.getMerchantById(null))
                                .thenThrow(new IllegalArgumentException("Merchant ID cannot be null or empty"));

                // Act & Assert
                HttpRequest<Object> request = HttpRequest.GET("/api/v1/merchants/null");
                HttpClientResponseException exception = assertThrows(
                                HttpClientResponseException.class,
                                () -> client.toBlocking().retrieve(request, ApiResponse.class));

                // Note: null in URL path becomes "null" string, which will trigger the same
                // validation
                assertThat((Object) exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        void testGetMerchantByIdEmptyId() {
                // Arrange - Empty string in path parameter
                when(merchantService.getMerchantById(""))
                                .thenThrow(new IllegalArgumentException("Merchant ID cannot be null or empty"));

                // Act & Assert
                HttpRequest<Object> request = HttpRequest.GET("/api/v1/merchants/");
                HttpClientResponseException exception = assertThrows(
                                HttpClientResponseException.class,
                                () -> client.toBlocking().retrieve(request, ApiResponse.class));

                // Note: This might result in 404 because the empty path doesn't match the route
                // or 400 if it matches and triggers validation
                assertThat((Object) exception.getStatus()).isIn(HttpStatus.NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        // ============ UPDATE MERCHANT TESTS ============

        @Test
        void testUpdateMerchantSuccess() {
                // Arrange - Update all fields
                MerchantResponse updatedMerchant = MerchantResponse.builder()
                                .merchantId("MCH-00001")
                                .merchantName("TechHub Electronics")
                                .businessName("TechHub Electronics LLC")
                                .email("updated@techhub.com")
                                .phone("+1-555-9999")
                                .businessType("retail")
                                .registrationNumber("REG-2024-001")
                                .isActive(false)
                                .createdAt(Instant.now())
                                .updatedAt(Instant.now())
                                .build();

                UpdateMerchantRequest updateRequest = UpdateMerchantRequest.builder()
                                .email("updated@techhub.com")
                                .phone("+1-555-9999")
                                .isActive(false)
                                .build();

                when(merchantService.updateMerchant("MCH-00001", updateRequest)).thenReturn(updatedMerchant);

                // Act
                HttpRequest<UpdateMerchantRequest> request = HttpRequest.PUT("/api/v1/merchants/MCH-00001",
                                updateRequest);
                ApiResponse<MerchantResponse> response = client.toBlocking().retrieve(request, ApiResponse.class);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.isSuccess()).isTrue();
                assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.getCode());
                assertThat(response.getMessage()).isEqualTo("Merchant updated successfully");

                verify(merchantService, times(1)).updateMerchant(eq("MCH-00001"), any(UpdateMerchantRequest.class));
        }

        @Test
        void testUpdateMerchantEmailOnly() {
                // Arrange - Update only email
                MerchantResponse updatedMerchant = MerchantResponse.builder()
                                .merchantId("MCH-00001")
                                .merchantName("TechHub Electronics")
                                .businessName("TechHub Electronics LLC")
                                .email("newemail@techhub.com")
                                .phone("+1-555-0101")
                                .businessType("retail")
                                .registrationNumber("REG-2024-001")
                                .isActive(true)
                                .createdAt(Instant.now())
                                .updatedAt(Instant.now())
                                .build();

                UpdateMerchantRequest updateRequest = UpdateMerchantRequest.builder()
                                .email("newemail@techhub.com")
                                .build();

                when(merchantService.updateMerchant("MCH-00001", updateRequest)).thenReturn(updatedMerchant);

                // Act
                HttpRequest<UpdateMerchantRequest> request = HttpRequest.PUT("/api/v1/merchants/MCH-00001",
                                updateRequest);
                ApiResponse<MerchantResponse> response = client.toBlocking().retrieve(request, ApiResponse.class);

                // Assert
                assertThat(response).isNotNull();
                assertThat(response.isSuccess()).isTrue();

                verify(merchantService, times(1)).updateMerchant(eq("MCH-00001"), any(UpdateMerchantRequest.class));
        }

        @Test
        void testUpdateMerchantNotFound() {
                // Arrange - Merchant doesn't exist
                UpdateMerchantRequest updateRequest = UpdateMerchantRequest.builder()
                                .email("test@example.com")
                                .build();

                when(merchantService.updateMerchant("MCH-99999", updateRequest))
                                .thenThrow(new NotFoundException("Merchant", "MCH-99999"));

                // Act & Assert
                HttpRequest<UpdateMerchantRequest> request = HttpRequest.PUT("/api/v1/merchants/MCH-99999",
                                updateRequest);
                HttpClientResponseException exception = assertThrows(
                                HttpClientResponseException.class,
                                () -> client.toBlocking().retrieve(request, ApiResponse.class));

                assertThat((Object) exception.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

                verify(merchantService, times(1)).updateMerchant(eq("MCH-99999"), any(UpdateMerchantRequest.class));
        }

        @Test
        void testUpdateMerchantInvalidEmail() {
                // Arrange - Invalid email format
                UpdateMerchantRequest updateRequest = UpdateMerchantRequest.builder()
                                .email("invalid-email")
                                .build();

                // Act & Assert - Validation should fail
                HttpRequest<UpdateMerchantRequest> request = HttpRequest.PUT("/api/v1/merchants/MCH-00001",
                                updateRequest);
                HttpClientResponseException exception = assertThrows(
                                HttpClientResponseException.class,
                                () -> client.toBlocking().retrieve(request, ApiResponse.class));

                assertThat((Object) exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

                // Service should not be called due to validation failure
                verify(merchantService, never()).updateMerchant(anyString(), any(UpdateMerchantRequest.class));
        }

        @Test
        void testUpdateMerchantNoFieldsProvided() {
                // Arrange - No fields provided
                UpdateMerchantRequest updateRequest = UpdateMerchantRequest.builder()
                                .build();

                // Act & Assert
                HttpRequest<UpdateMerchantRequest> request = HttpRequest.PUT("/api/v1/merchants/MCH-00001",
                                updateRequest);
                HttpClientResponseException exception = assertThrows(
                                HttpClientResponseException.class,
                                () -> client.toBlocking().retrieve(request, ApiResponse.class));

                assertThat((Object) exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

                // Service should not be called
                verify(merchantService, never()).updateMerchant(anyString(), any(UpdateMerchantRequest.class));
        }

        @Test
        void testUpdateMerchantInvalidId() {
                // Arrange - Invalid merchant ID format
                UpdateMerchantRequest updateRequest = UpdateMerchantRequest.builder()
                                .email("test@example.com")
                                .build();

                when(merchantService.updateMerchant("INVALID-ID", updateRequest))
                                .thenThrow(new IllegalArgumentException(
                                                "Invalid merchant ID format. Expected format: MCH-XXXXX (e.g., MCH-00001)"));

                // Act & Assert
                HttpRequest<UpdateMerchantRequest> request = HttpRequest.PUT("/api/v1/merchants/INVALID-ID",
                                updateRequest);
                HttpClientResponseException exception = assertThrows(
                                HttpClientResponseException.class,
                                () -> client.toBlocking().retrieve(request, ApiResponse.class));

                assertThat((Object) exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

                verify(merchantService, times(1)).updateMerchant(eq("INVALID-ID"), any(UpdateMerchantRequest.class));
        }

        // ===== CREATE MERCHANT TESTS =====

        @Test
        void testCreateMerchantSuccess() {
                CreateMerchantRequest createRequest = CreateMerchantRequest.builder()
                                .merchantName("TechHub Electronics")
                                .businessName("TechHub Electronics LLC")
                                .email("newemail1@example.com")
                                .phone("9818576955")
                                .businessType("retail")
                                .taxId("123456789")
                                .registrationNumber("REG-2024-001")
                                .build();

                MerchantResponse createdMerchant = MerchantResponse.builder()
                                .merchantId("MCH-00001")
                                .merchantName("TechHub Electronics")
                                .businessName("TechHub Electronics LLC")
                                .email("newemail1@example.com")
                                .phone("9818576955")
                                .businessType("retail")
                                .taxId("123456789")
                                .registrationNumber("REG-2024-001")
                                .isActive(true)
                                .build();

                when(merchantService.createMerchant(any(CreateMerchantRequest.class))).thenReturn(createdMerchant);

                HttpRequest<CreateMerchantRequest> request = HttpRequest.POST("/api/v1/merchants", createRequest);
                HttpResponse<ApiResponse> response = client.toBlocking().exchange(request, ApiResponse.class);

                assertThat((Object) response.getStatus()).isEqualTo(HttpStatus.CREATED);
                assertThat(response.body()).isNotNull();
                assertThat(response.body().isSuccess()).isTrue();
                assertThat(response.body().getMessage()).isEqualTo("Merchant created successfully");

                verify(merchantService, times(1)).createMerchant(any(CreateMerchantRequest.class));
        }

        @Test
        void testCreateMerchantInvalidEmail() {
                CreateMerchantRequest createRequest = CreateMerchantRequest.builder()
                                .merchantName("Test Merchant")
                                .businessName("Test LLC")
                                .email("invalid-email")
                                .phone("1234567890")
                                .businessType("retail")
                                .build();

                HttpRequest<CreateMerchantRequest> request = HttpRequest.POST("/api/v1/merchants", createRequest);

                HttpClientResponseException exception = assertThrows(
                                HttpClientResponseException.class,
                                () -> client.toBlocking().exchange(request, ApiResponse.class));

                assertThat((Object) exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

                verify(merchantService, never()).createMerchant(any(CreateMerchantRequest.class));
        }

        @Test
        void testCreateMerchantDuplicateEmail() {
                CreateMerchantRequest createRequest = CreateMerchantRequest.builder()
                                .merchantName("Duplicate Merchant")
                                .businessName("Duplicate LLC")
                                .email("existing@example.com")
                                .phone("9999999999")
                                .businessType("retail")
                                .build();

                when(merchantService.createMerchant(any(CreateMerchantRequest.class)))
                                .thenThrow(new IllegalArgumentException("Email already exists: existing@example.com"));

                HttpRequest<CreateMerchantRequest> request = HttpRequest.POST("/api/v1/merchants", createRequest);

                HttpClientResponseException exception = assertThrows(
                                HttpClientResponseException.class,
                                () -> client.toBlocking().exchange(request, ApiResponse.class));

                assertThat((Object) exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

                verify(merchantService, times(1)).createMerchant(any(CreateMerchantRequest.class));
        }

        @Test
        void testCreateMerchantMissingRequiredFields() {
                CreateMerchantRequest createRequest = CreateMerchantRequest.builder()
                                .merchantName("Test Merchant")
                                // Missing businessName, email, phone, businessType
                                .build();

                HttpRequest<CreateMerchantRequest> request = HttpRequest.POST("/api/v1/merchants", createRequest);

                HttpClientResponseException exception = assertThrows(
                                HttpClientResponseException.class,
                                () -> client.toBlocking().exchange(request, ApiResponse.class));

                assertThat((Object) exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

                verify(merchantService, never()).createMerchant(any(CreateMerchantRequest.class));
        }

        @Test
        void testCreateMerchantInvalidBusinessType() {
                CreateMerchantRequest createRequest = CreateMerchantRequest.builder()
                                .merchantName("Test Merchant")
                                .businessName("Test LLC")
                                .email("test@example.com")
                                .phone("1234567890")
                                .businessType("invalid-type")
                                .build();

                HttpRequest<CreateMerchantRequest> request = HttpRequest.POST("/api/v1/merchants", createRequest);

                HttpClientResponseException exception = assertThrows(
                                HttpClientResponseException.class,
                                () -> client.toBlocking().exchange(request, ApiResponse.class));

                assertThat((Object) exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

                verify(merchantService, never()).createMerchant(any(CreateMerchantRequest.class));
        }
}
