package com.payment.dto.merchant;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Request object for updating merchant information")
public class UpdateMerchantRequest {

    @Email(message = "Email must be valid")
    @Schema(description = "New email address for the merchant", example = "newemail@example.com", nullable = true)
    private String email;

    @Schema(description = "New phone number for the merchant", example = "+1-555-9999", nullable = true)
    private String phone;

    @Schema(description = "New active status for the merchant", example = "false", nullable = true)
    private Boolean isActive;

    /**
     * Validates that at least one field is provided for update
     * 
     * @return true if at least one field is not null
     */
    public boolean hasAtLeastOneField() {
        return email != null || phone != null || isActive != null;
    }
}
