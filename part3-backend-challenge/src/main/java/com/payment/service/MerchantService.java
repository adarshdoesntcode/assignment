package com.payment.service;

import com.payment.dto.common.PaginationDTO;
import com.payment.dto.merchant.CreateMerchantRequest;
import com.payment.dto.merchant.MerchantListResponse;
import com.payment.dto.merchant.MerchantResponse;
import com.payment.dto.merchant.MerchantSearchRequest;
import com.payment.dto.merchant.UpdateMerchantRequest;
import com.payment.entity.Merchant;
import com.payment.exception.NotFoundException;
import com.payment.repository.MerchantRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class MerchantService {

    private static final Logger LOG = LoggerFactory.getLogger(MerchantService.class);

    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    /**
     * Get merchant by ID
     * 
     * @param merchantId The merchant ID to search for
     * @return MerchantResponse DTO
     * @throws IllegalArgumentException if merchantId format is invalid
     * @throws NotFoundException        if merchant is not found or inactive
     */
    public MerchantResponse getMerchantById(String merchantId) {
        LOG.info("Fetching merchant with ID: {}", merchantId);

        // Validate merchantId format
        if (merchantId == null || merchantId.isBlank()) {
            throw new IllegalArgumentException("Merchant ID cannot be null or empty");
        }

        if (!merchantId.matches("^MCH-\\d{5}$")) {
            throw new IllegalArgumentException(
                    "Invalid merchant ID format. Expected format: MCH-XXXXX (e.g., MCH-00001)");
        }

        // Find merchant by ID
        Optional<Merchant> merchantOptional = merchantRepository.findById(merchantId);

        // Check if merchant exists
        if (merchantOptional.isEmpty()) {
            LOG.warn("Merchant not found with ID: {}", merchantId);
            throw new NotFoundException("Merchant", merchantId);
        }

        Merchant merchant = merchantOptional.get();

        LOG.info("Successfully retrieved merchant: {} (active: {})", merchantId, merchant.getIsActive());
        return convertToMerchantResponse(merchant);
    }

    /**
     * Get merchants with filtering, sorting, and pagination
     */
    public MerchantListResponse getMerchants(MerchantSearchRequest request) {
        LOG.info("Fetching merchants - name: {}, id: {}, page: {}, size: {}",
                request.getMerchantName(), request.getMerchantId(),
                request.getPage(), request.getSize());

        // Build pageable with sorting
        Pageable pageable = buildPageable(request);

        // Fetch merchants based on filters
        Page<Merchant> merchantPage = fetchMerchants(request, pageable);

        // Convert to response DTOs
        List<MerchantResponse> merchantResponses = merchantPage.getContent().stream()
                .map(this::convertToMerchantResponse)
                .collect(Collectors.toList());

        LOG.info("Returning {} merchants out of {} total",
                merchantResponses.size(), merchantPage.getTotalSize());

        // Build response
        return MerchantListResponse.builder()
                .merchants(merchantResponses)
                .pagination(PaginationDTO.builder()
                        .page(request.getPage())
                        .size(request.getSize())
                        .totalPages(merchantPage.getTotalPages())
                        .totalElements(merchantPage.getTotalSize())
                        .build())
                .build();
    }

    /**
     * Build Pageable with multi-sort configuration
     */
    private Pageable buildPageable(MerchantSearchRequest request) {
        String[] sortFields = request.getSortBy().split(",");
        String[] sortDirections = request.getSortDirection().split(",");

        // Build list of Sort.Order
        List<Sort.Order> orders = new java.util.ArrayList<>();

        for (int i = 0; i < sortFields.length; i++) {
            String field = mapSortField(sortFields[i].trim());
            String direction = i < sortDirections.length ? sortDirections[i].trim() : "ASC";

            if ("DESC".equalsIgnoreCase(direction)) {
                orders.add(Sort.Order.desc(field));
            } else {
                orders.add(Sort.Order.asc(field));
            }
        }

        return Pageable.from(request.getPage(), request.getSize(), Sort.of(orders));
    }

    /**
     * Validate and return sort field (entity property names, not database columns)
     */
    private String mapSortField(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "createdAt";
        }

        // Micronaut Data expects entity property names (camelCase), not database column
        // names
        return switch (sortBy) {
            case "merchantId", "merchantName", "businessName", "businessType", "createdAt" -> sortBy;
            default -> "createdAt";
        };
    }

    /**
     * Fetch merchants based on provided filters
     */
    private Page<Merchant> fetchMerchants(MerchantSearchRequest request, Pageable pageable) {
        String merchantName = request.getMerchantName();
        String merchantId = request.getMerchantId();
        Boolean isActive = request.getIsActive();

        // If merchant ID is provided, search by ID (with optional isActive filter)
        if (merchantId != null && !merchantId.isBlank()) {
            if (isActive != null) {
                return merchantRepository.findByMerchantIdAndIsActive(merchantId, isActive, pageable);
            }
            return merchantRepository.findByMerchantId(merchantId, pageable);
        }

        // If merchant name is provided, search by name (with optional isActive filter)
        if (merchantName != null && !merchantName.isBlank()) {
            if (isActive != null) {
                return merchantRepository.findByMerchantNameContainsIgnoreCaseAndIsActive(
                        merchantName, isActive, pageable);
            }
            return merchantRepository.findByMerchantNameContainsIgnoreCase(merchantName, pageable);
        }

        // No name/ID filters - return all merchants (with optional isActive filter)
        if (isActive != null) {
            return merchantRepository.findByIsActive(isActive, pageable);
        }

        return merchantRepository.findAll(pageable);
    }

    /**
     * Update merchant information (email, phone, isActive)
     * 
     * @param merchantId The merchant ID to update
     * @param request    Request containing fields to update
     * @return Updated MerchantResponse DTO
     * @throws IllegalArgumentException if merchantId format is invalid or no fields
     *                                  to update
     * @throws NotFoundException        if merchant is not found
     */
    public MerchantResponse updateMerchant(String merchantId, UpdateMerchantRequest request) {
        LOG.info("Updating merchant with ID: {}", merchantId);

        // Validate merchantId format
        if (merchantId == null || merchantId.isBlank()) {
            throw new IllegalArgumentException("Merchant ID cannot be null or empty");
        }

        if (!merchantId.matches("^MCH-\\d{5}$")) {
            throw new IllegalArgumentException(
                    "Invalid merchant ID format. Expected format: MCH-XXXXX (e.g., MCH-00001)");
        }

        // Validate that at least one field is provided
        if (!request.hasAtLeastOneField()) {
            throw new IllegalArgumentException(
                    "At least one field (email, phone, or isActive) must be provided for update");
        }

        // Find merchant by ID
        Optional<Merchant> merchantOptional = merchantRepository.findById(merchantId);

        // Check if merchant exists
        if (merchantOptional.isEmpty()) {
            LOG.warn("Merchant not found with ID: {}", merchantId);
            throw new NotFoundException("Merchant", merchantId);
        }

        Merchant merchant = merchantOptional.get();

        // Update only provided fields
        boolean updated = false;

        if (request.getEmail() != null) {
            LOG.info("Updating email for merchant {}: {} -> {}",
                    merchantId, merchant.getEmail(), request.getEmail());
            merchant.setEmail(request.getEmail());
            updated = true;
        }

        if (request.getPhone() != null) {
            LOG.info("Updating phone for merchant {}: {} -> {}",
                    merchantId, merchant.getPhone(), request.getPhone());
            merchant.setPhone(request.getPhone());
            updated = true;
        }

        if (request.getIsActive() != null) {
            LOG.info("Updating isActive status for merchant {}: {} -> {}",
                    merchantId, merchant.getIsActive(), request.getIsActive());
            merchant.setIsActive(request.getIsActive());
            updated = true;
        }

        // Save updated merchant
        if (updated) {
            Merchant savedMerchant = merchantRepository.update(merchant);
            LOG.info("Successfully updated merchant: {}", merchantId);
            return convertToMerchantResponse(savedMerchant);
        }

        // This should not happen due to hasAtLeastOneField check, but added for safety
        return convertToMerchantResponse(merchant);
    }

    /**
     * Convert Merchant entity to MerchantResponse DTO
     */
    private MerchantResponse convertToMerchantResponse(Merchant merchant) {
        return MerchantResponse.builder()
                .merchantId(merchant.getMerchantId())
                .merchantName(merchant.getMerchantName())
                .businessName(merchant.getBusinessName())
                .email(merchant.getEmail())
                .phone(merchant.getPhone())
                .businessType(merchant.getBusinessType())
                .taxId(merchant.getTaxId())
                .registrationNumber(merchant.getRegistrationNumber())
                .isActive(merchant.getIsActive())
                .createdAt(merchant.getCreatedAt())
                .updatedAt(merchant.getUpdatedAt())
                .build();
    }

    /**
     * Create a new merchant
     * 
     * @param request CreateMerchantRequest containing merchant details
     * @return MerchantResponse DTO of the created merchant
     * @throws IllegalArgumentException if email already exists
     */
    public MerchantResponse createMerchant(CreateMerchantRequest request) {
        LOG.info("Creating new merchant with email: {}", request.getEmail());

        // Check for duplicate email
        Long emailCount = merchantRepository.countByEmail(request.getEmail());
        if (emailCount != null && emailCount > 0) {
            LOG.warn("Duplicate email detected: {}", request.getEmail());
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        // Check for duplicate taxId
        Long taxIdCount = merchantRepository.countByTaxId(request.getTaxId());
        if (taxIdCount != null && taxIdCount > 0) {
            LOG.warn("Duplicate taxId detected: {}", request.getTaxId());
            throw new IllegalArgumentException("Tax ID already exists: " + request.getTaxId());
        }

        // Check for duplicate registrationNumber
        Long regNumberCount = merchantRepository.countByRegistrationNumber(request.getRegistrationNumber());
        if (regNumberCount != null && regNumberCount > 0) {
            LOG.warn("Duplicate registrationNumber detected: {}", request.getRegistrationNumber());
            throw new IllegalArgumentException(
                    "Registration number already exists: " + request.getRegistrationNumber());
        }

        // Generate merchant ID
        String newMerchantId = generateMerchantId();
        LOG.info("Generated merchant ID: {}", newMerchantId);

        // Create merchant entity
        Merchant merchant = Merchant.builder()
                .merchantId(newMerchantId)
                .merchantName(request.getMerchantName())
                .businessName(request.getBusinessName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .businessType(request.getBusinessType())
                .taxId(request.getTaxId())
                .registrationNumber(request.getRegistrationNumber())
                .isActive(true)
                .build();

        // Save to database
        Merchant savedMerchant = merchantRepository.save(merchant);
        LOG.info("Successfully created merchant: {}", savedMerchant.getMerchantId());

        // Convert to response DTO
        return convertToMerchantResponse(savedMerchant);
    }

    /**
     * Generate the next merchant ID in format MCH-XXXXX
     * 
     * @return Next merchant ID
     */
    private String generateMerchantId() {
        Optional<Merchant> latestMerchant = merchantRepository.findLatestMerchant();

        if (latestMerchant.isPresent()) {
            String latestId = latestMerchant.get().getMerchantId();
            // Extract numeric part (e.g., "00001" from "MCH-00001")
            String numericPart = latestId.substring(4); // Skip "MCH-"
            int nextNumber = Integer.parseInt(numericPart) + 1;
            // Format with zero-padding to 5 digits
            return String.format("MCH-%05d", nextNumber);
        } else {
            // No merchants exist yet, start with MCH-00001
            return "MCH-00001";
        }
    }

}
