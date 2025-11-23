package com.payment.controller;

import com.payment.dto.ApiResponse;
import com.payment.dto.MerchantListResponse;
import com.payment.dto.MerchantSearchRequest;
import com.payment.service.MerchantService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

        @Get
        @Operation(summary = "Get all merchants", description = "Retrieve merchants with optional filtering, sorting, and pagination")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Merchants retrieved successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request parameters")
        })
        public HttpResponse<ApiResponse<MerchantListResponse>> getMerchants(
                        @QueryValue Optional<String> merchantName,
                        @QueryValue Optional<String> merchantId,
                        @QueryValue Optional<String> sortBy,
                        @QueryValue Optional<String> sortDirection,
                        @QueryValue Optional<Integer> page,
                        @QueryValue Optional<Integer> size) {

                LOG.info("GET /api/v1/merchants - merchantName: {}, merchantId: {}, " +
                                "sortBy: {}, sortDirection: {}, page: {}, size: {}",
                                merchantName.orElse(null), merchantId.orElse(null),
                                sortBy.orElse(null), sortDirection.orElse(null),
                                page.orElse(null), size.orElse(null));

                // Build request object
                MerchantSearchRequest request = MerchantSearchRequest.builder()
                                .merchantName(merchantName.orElse(null))
                                .merchantId(merchantId.orElse(null))
                                .sortBy(sortBy.orElse("merchantId"))
                                .sortDirection(sortDirection.orElse("ASC"))
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
}
