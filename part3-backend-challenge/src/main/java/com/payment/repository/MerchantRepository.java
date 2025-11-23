package com.payment.repository;

import com.payment.entity.Merchant;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.PageableRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MerchantRepository extends PageableRepository<Merchant, String> {

        /**
         * Find all merchants with pagination
         */
        Page<Merchant> findAll(Pageable pageable);

        /**
         * Find merchants by name (case-insensitive partial match)
         */
        @Query(value = "SELECT merchant_.* FROM operators.merchants merchant_ WHERE LOWER(merchant_.merchant_name) LIKE LOWER(CONCAT('%', :merchantName, '%'))", countQuery = "SELECT COUNT(*) FROM operators.merchants WHERE LOWER(merchant_name) LIKE LOWER(CONCAT('%', :merchantName, '%'))")
        Page<Merchant> findByMerchantNameContainsIgnoreCase(String merchantName, Pageable pageable);

        /**
         * Find merchant by exact ID
         */
        @Query(value = "SELECT merchant_.* FROM operators.merchants merchant_ WHERE merchant_.merchant_id = :merchantId", countQuery = "SELECT COUNT(*) FROM operators.merchants WHERE merchant_id = :merchantId")
        Page<Merchant> findByMerchantId(String merchantId, Pageable pageable);

        /**
         * Find merchants by business type
         */
        @Query(value = "SELECT merchant_.* FROM operators.merchants merchant_ WHERE merchant_.business_type = :businessType", countQuery = "SELECT COUNT(*) FROM operators.merchants WHERE business_type = :businessType")
        Page<Merchant> findByBusinessType(String businessType, Pageable pageable);

        /**
         * Find merchants by active status
         */
        @Query(value = "SELECT merchant_.* FROM operators.merchants merchant_ WHERE merchant_.is_active = :isActive", countQuery = "SELECT COUNT(*) FROM operators.merchants WHERE is_active = :isActive")
        Page<Merchant> findByIsActive(Boolean isActive, Pageable pageable);

        /**
         * Find merchants by name and business type
         */
        @Query(value = "SELECT merchant_.* FROM operators.merchants merchant_ WHERE LOWER(merchant_.merchant_name) LIKE LOWER(CONCAT('%', :merchantName, '%')) AND merchant_.business_type = :businessType", countQuery = "SELECT COUNT(*) FROM operators.merchants WHERE LOWER(merchant_name) LIKE LOWER(CONCAT('%', :merchantName, '%')) AND business_type = :businessType")
        Page<Merchant> findByMerchantNameContainsIgnoreCaseAndBusinessType(String merchantName, String businessType,
                        Pageable pageable);

        /**
         * Find merchants by name and active status
         */
        @Query(value = "SELECT merchant_.* FROM operators.merchants merchant_ WHERE LOWER(merchant_.merchant_name) LIKE LOWER(CONCAT('%', :merchantName, '%')) AND merchant_.is_active = :isActive", countQuery = "SELECT COUNT(*) FROM operators.merchants WHERE LOWER(merchant_name) LIKE LOWER(CONCAT('%', :merchantName, '%')) AND is_active = :isActive")
        Page<Merchant> findByMerchantNameContainsIgnoreCaseAndIsActive(String merchantName, Boolean isActive,
                        Pageable pageable);

        /**
         * Find merchant by ID and active status
         */
        @Query(value = "SELECT merchant_.* FROM operators.merchants merchant_ WHERE merchant_.merchant_id = :merchantId AND merchant_.is_active = :isActive", countQuery = "SELECT COUNT(*) FROM operators.merchants WHERE merchant_id = :merchantId AND is_active = :isActive")
        Page<Merchant> findByMerchantIdAndIsActive(String merchantId, Boolean isActive, Pageable pageable);

        /**
         * Find merchants by business type and active status
         */
        @Query(value = "SELECT merchant_.* FROM operators.merchants merchant_ WHERE merchant_.business_type = :businessType AND merchant_.is_active = :isActive", countQuery = "SELECT COUNT(*) FROM operators.merchants WHERE business_type = :businessType AND is_active = :isActive")
        Page<Merchant> findByBusinessTypeAndIsActive(String businessType, Boolean isActive, Pageable pageable);

        /**
         * Find merchants by all filters
         */
        @Query(value = "SELECT merchant_.* FROM operators.merchants merchant_ WHERE LOWER(merchant_.merchant_name) LIKE LOWER(CONCAT('%', :merchantName, '%')) AND merchant_.business_type = :businessType AND merchant_.is_active = :isActive", countQuery = "SELECT COUNT(*) FROM operators.merchants WHERE LOWER(merchant_name) LIKE LOWER(CONCAT('%', :merchantName, '%')) AND business_type = :businessType AND is_active = :isActive")
        Page<Merchant> findByMerchantNameContainsIgnoreCaseAndBusinessTypeAndIsActive(String merchantName,
                        String businessType, Boolean isActive, Pageable pageable);

        /**
         * Find the latest merchant (highest merchantId) for ID generation
         */
        @Query("SELECT merchant_.* FROM operators.merchants merchant_ ORDER BY merchant_.merchant_id DESC LIMIT 1")
        java.util.Optional<Merchant> findLatestMerchant();

        /**
         * Check if email already exists
         */
        @Query("SELECT COUNT(*) FROM operators.merchants WHERE email = :email")
        Long countByEmail(String email);

        /**
         * Check if taxId already exists
         */
        @Query("SELECT COUNT(*) FROM operators.merchants WHERE tax_id = :taxId")
        Long countByTaxId(String taxId);

        /**
         * Check if registrationNumber already exists
         */
        @Query("SELECT COUNT(*) FROM operators.merchants WHERE registration_number = :registrationNumber")
        Long countByRegistrationNumber(String registrationNumber);
}
