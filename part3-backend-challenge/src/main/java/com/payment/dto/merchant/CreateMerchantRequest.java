package com.payment.dto.merchant;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new merchant
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Request to create a new merchant")
public class CreateMerchantRequest {

    @NotBlank(message = "Merchant name is required")
    @Schema(description = "Name of the merchant", example = "TechHub Electronics")
    private String merchantName;

    @NotBlank(message = "Business name is required")
    @Schema(description = "Legal business name", example = "TechHub Electronics LLC")
    private String businessName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(description = "Contact email address", example = "contact@techhub.com")
    private String email;

    @NotBlank(message = "Phone is required")
    @Schema(description = "Contact phone number", example = "9818576955")
    private String phone;

    @NotBlank(message = "Business type is required")
    @Pattern(regexp = "^(retail|restaurant|ecommerce|services|hospitality|healthcare|other)$", message = "Business type must be one of: retail, restaurant, ecommerce, services, hospitality, healthcare, other")
    @Schema(description = "Type of business", example = "retail", allowableValues = { "retail",
            "restaurant", "ecommerce", "services", "hospitality", "healthcare", "other" })
    private String businessType;

    @NotBlank(message = "Tax ID is required")
    @Schema(description = "Tax identification number", example = "123456789")
    private String taxId;

    @NotBlank(message = "Registration number is required")
    @Schema(description = "Business registration number", example = "REG-2024-001")
    private String registrationNumber;
}
