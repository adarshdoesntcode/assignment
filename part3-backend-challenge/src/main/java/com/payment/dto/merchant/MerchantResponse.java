package com.payment.dto.merchant;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Merchant response object")
public class MerchantResponse {

    @Schema(description = "Merchant ID", example = "MCH-00001")
    private String merchantId;

    @Schema(description = "Merchant name", example = "TechHub Electronics")
    private String merchantName;

    @Schema(description = "Business name", example = "TechHub Electronics LLC")
    private String businessName;

    @Schema(description = "Email address", example = "contact@techhub.com")
    private String email;

    @Schema(description = "Phone number", example = "+1-555-0101")
    private String phone;

    @Schema(description = "Business type", example = "retail")
    private String businessType;

    @Schema(description = "Tax ID", example = "TAX-12345")
    private String taxId;

    @Schema(description = "Registration number", example = "REG-2024-001")
    private String registrationNumber;

    @Schema(description = "Merchant active status", example = "true")
    private Boolean isActive;

    @Schema(description = "Creation timestamp", example = "2024-05-27T10:15:30Z")
    private Instant createdAt;

    @Schema(description = "Last update timestamp", example = "2024-11-22T14:30:00Z")
    private Instant updatedAt;
}
