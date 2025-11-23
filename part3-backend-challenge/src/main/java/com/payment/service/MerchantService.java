package com.payment.service;

import com.payment.dto.MerchantListResponse;
import com.payment.dto.MerchantResponse;
import com.payment.dto.MerchantSearchRequest;
import com.payment.dto.PaginationDTO;
import com.payment.entity.Merchant;
import com.payment.repository.MerchantRepository;
import jakarta.inject.Singleton;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class MerchantService {

    private static final Logger LOG = LoggerFactory.getLogger(MerchantService.class);

    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
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
            return "merchantId";
        }

        // Micronaut Data expects entity property names (camelCase), not database column
        // names
        return switch (sortBy) {
            case "merchantId", "merchantName", "businessName", "businessType", "createdAt" -> sortBy;
            default -> "merchantId";
        };
    }

    /**
     * Fetch merchants based on provided filters (active merchants only)
     */
    private Page<Merchant> fetchMerchants(MerchantSearchRequest request, Pageable pageable) {
        String merchantName = request.getMerchantName();
        String merchantId = request.getMerchantId();

        // If merchant ID is provided, search by ID only (exact match, active only)
        if (merchantId != null && !merchantId.isBlank()) {
            return merchantRepository.findByMerchantIdAndIsActive(merchantId, true, pageable);
        }

        // If merchant name is provided, search by name (active only)
        if (merchantName != null && !merchantName.isBlank()) {
            return merchantRepository.findByMerchantNameContainsIgnoreCaseAndIsActive(
                    merchantName, true, pageable);
        }

        // No filters - return all active merchants
        return merchantRepository.findByIsActive(true, pageable);
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
                .registrationNumber(merchant.getRegistrationNumber())
                .createdAt(merchant.getCreatedAt())
                .updatedAt(merchant.getUpdatedAt())
                .build();
    }
}
