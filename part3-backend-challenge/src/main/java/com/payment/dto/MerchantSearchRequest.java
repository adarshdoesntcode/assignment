package com.payment.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Introspected
@Schema(description = "Request object for searching and filtering merchants")
public class MerchantSearchRequest {

        @Schema(description = "Merchant name (partial match, case-insensitive)", example = "TechHub")
        private String merchantName;

        @Schema(description = "Merchant ID (exact match)", example = "MCH-00001")
        @Pattern(regexp = "^MCH-\\d{5}$", message = "Merchant ID must be in format MCH-XXXXX")
        private String merchantId;

        @Schema(description = "Comma-separated fields to sort by", example = "merchantName,createdAt", allowableValues = {
                        "merchantId", "merchantName", "businessName", "businessType", "createdAt" })
        @Builder.Default
        private String sortBy = "merchantId";

        @Schema(description = "Comma-separated sort directions (ASC or DESC)", example = "ASC,DESC")
        @Builder.Default
        private String sortDirection = "ASC";

        @Schema(description = "Page number (0-indexed)", example = "0")
        @Min(value = 0, message = "Page must be greater than or equal to 0")
        @Builder.Default
        private Integer page = 0;

        @Schema(description = "Page size", example = "20")
        @Min(value = 1, message = "Size must be greater than or equal to 1")
        @Builder.Default
        private Integer size = 20;
}
