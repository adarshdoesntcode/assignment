package com.payment.controller;

import com.payment.dto.common.ApiResponse;
import com.payment.dto.merchant.CreateMerchantRequest;
import com.payment.dto.merchant.MerchantListResponse;
import com.payment.dto.merchant.MerchantResponse;
import com.payment.dto.merchant.MerchantSearchRequest;
import com.payment.dto.merchant.UpdateMerchantRequest;
import com.payment.service.MerchantService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Controller("/api/v1/merchants")
@Tag(name = "Merchants", description = "Merchant management endpoints")
public class MerchantController {

        private static final Logger LOG = LoggerFactory.getLogger(MerchantController.class);

        private final MerchantService merchantService;

        public MerchantController(MerchantService merchantService) {
                this.merchantService = merchantService;
        }

        @Post
        @Operation(summary = "Create new merchant", description = "Creates a new merchant with auto-generated merchant ID in format MCH-XXXXX")
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Merchant created successfully")
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data or duplicate email")
        public HttpResponse<ApiResponse<MerchantResponse>> createMerchant(@Valid @Body CreateMerchantRequest request) {
                LOG.info("POST /api/v1/merchants - Creating merchant: {}", request.getMerchantName());

                try {
                        MerchantResponse merchantResponse = merchantService.createMerchant(request);
                        ApiResponse<MerchantResponse> response = new ApiResponse<>(
                                        HttpStatus.CREATED.getCode(),
                                        true,
                                        "Merchant created successfully",
                                        merchantResponse);
                        return HttpResponse.created(response);
                } catch (IllegalArgumentException e) {
                        LOG.error("Invalid request: {}", e.getMessage());
                        ApiResponse<MerchantResponse> response = new ApiResponse<>(
                                        HttpStatus.BAD_REQUEST.getCode(),
                                        false,
                                        e.getMessage(),
                                        null);
                        return HttpResponse.badRequest(response);
                }
        }

        @Get
        @Operation(summary = "Get all merchants", description = "Retrieve merchants with optional filtering, sorting, and pagination")
        public HttpResponse<ApiResponse<MerchantListResponse>> getMerchants(
                        @QueryValue Optional<String> merchantName,
                        @QueryValue Optional<String> merchantId,
                        @QueryValue Optional<Boolean> isActive,
                        @QueryValue Optional<String> sortBy,
                        @QueryValue Optional<String> sortDirection,
                        @QueryValue Optional<Integer> page,
                        @QueryValue Optional<Integer> size) {

                LOG.info("GET /api/v1/merchants - merchantName: {}, merchantId: {}, isActive: {}, " +
                                "sortBy: {}, sortDirection: {}, page: {}, size: {}",
                                merchantName.orElse(null), merchantId.orElse(null), isActive.orElse(null),
                                sortBy.orElse(null), sortDirection.orElse(null),
                                page.orElse(null), size.orElse(null));

                // Build request object
                MerchantSearchRequest request = MerchantSearchRequest.builder()
                                .merchantName(merchantName.orElse(null))
                                .merchantId(merchantId.orElse(null))
                                .isActive(isActive.orElse(null))
                                .sortBy(sortBy.orElse("createdAt"))
                                .sortDirection(sortDirection.orElse("DESC"))
                                .page(page.orElse(0))
                                .size(size.orElse(20))
                                .build();

                // Get merchants
                MerchantListResponse response = merchantService.getMerchants(request);

                LOG.info("Returning {} merchants", response.getMerchants().size());

                return HttpResponse.ok(ApiResponse.success(
                                HttpStatus.OK.getCode(),
                                "Merchants retrieved successfully",
                                response));
        }

        @Get("/{merchantId}")
        @Operation(summary = "Get merchant by ID", description = "Retrieve details of a specific merchant by merchant ID")
        public HttpResponse<ApiResponse<MerchantResponse>> getMerchantById(String merchantId) {

                LOG.info("GET /api/v1/merchants/{} - Fetching merchant details", merchantId);

                // Get merchant by ID
                MerchantResponse response = merchantService.getMerchantById(merchantId);

                LOG.info("Successfully retrieved merchant: {}", merchantId);

                return HttpResponse.ok(ApiResponse.success(
                                HttpStatus.OK.getCode(),
                                "Merchant retrieved successfully",
                                response));
        }

        @Put("/{merchantId}")
        @Operation(summary = "Update merchant", description = "Update merchant email, phone, and/or active status")
        public HttpResponse<ApiResponse<MerchantResponse>> updateMerchant(
                        String merchantId,
                        @Valid @Body UpdateMerchantRequest request) {

                LOG.info("PUT /api/v1/merchants/{} - Updating merchant", merchantId);

                // Validate that at least one field is provided
                if (!request.hasAtLeastOneField()) {
                        LOG.warn("No fields provided for update");
                        return HttpResponse.badRequest(ApiResponse.error(
                                        HttpStatus.BAD_REQUEST.getCode(),
                                        "At least one field (email, phone, or isActive) must be provided for update"));
                }

                // Update merchant
                MerchantResponse response = merchantService.updateMerchant(merchantId, request);

                LOG.info("Successfully updated merchant: {}", merchantId);

                return HttpResponse.ok(ApiResponse.success(
                                HttpStatus.OK.getCode(),
                                "Merchant updated successfully",
                                response));
        }
}
