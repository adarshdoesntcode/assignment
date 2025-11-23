package com.payment.entity;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Id;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
@MappedEntity(value = "merchants", schema = "operators")
public class Merchant {

    @Id
    @NotBlank(message = "Merchant ID is required")
    @Pattern(regexp = "^MCH-\\d{5}$", message = "Merchant ID must be in format MCH-XXXXX")
    private String merchantId;

    @NotBlank(message = "Merchant name is required")
    private String merchantName;

    @NotBlank(message = "Business name is required")
    private String businessName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Business type is required")
    @Pattern(regexp = "^(retail|restaurant|ecommerce|services|hospitality|healthcare|other)$", message = "Business type must be one of: retail, restaurant, ecommerce, services, hospitality, healthcare, other")
    private String businessType;

    private String taxId;

    private String registrationNumber;

    @Builder.Default
    private Boolean isActive = true;

    @DateCreated
    private Instant createdAt;

    @DateUpdated
    private Instant updatedAt;
}
