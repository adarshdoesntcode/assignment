package com.payment.mapper;

import com.payment.dto.TransactionResponse.TransactionDetailDTO;
import com.payment.entity.TransactionMaster;
import jakarta.inject.Singleton;

/**
 * Mapper to convert between TransactionMaster entity and TransactionResponseDTO
 */
@Singleton
public class TransactionMapper {

    /**
     * Convert TransactionMaster entity to TransactionDetailDTO
     * @param entity The entity to convert
     * @return The DTO representation
     */
    public TransactionDetailDTO toTransactionDetailDTO(TransactionMaster entity) {
        if (entity == null) {
            return null;
        }

        return TransactionDetailDTO.builder()
                .txnId(entity.getTxnId())
                .merchantId(entity.getMerchantId())
                .gpAcquirerId(entity.getGpAcquirerId())
                .gpIssuerId(entity.getGpIssuerId())
                .txnDate(entity.getTxnDate() != null ? entity.getTxnDate().toLocalDate() : null)
                .localTxnDateTime(entity.getLocalTxnDateTime())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .status(entity.getStatus())
                .cardType(entity.getCardType())
                .cardLast4(entity.getCardLast4())
                .authCode(entity.getAuthCode())
                .responseCode(entity.getResponseCode())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * Convert TransactionDetailDTO to TransactionMaster entity
     * @param dto The DTO to convert
     * @return The entity representation
     */
    public TransactionMaster toEntity(TransactionDetailDTO dto) {
        if (dto == null) {
            return null;
        }

        TransactionMaster entity = new TransactionMaster();
        entity.setTxnId(dto.getTxnId());
        entity.setMerchantId(dto.getMerchantId());
        entity.setGpAcquirerId(dto.getGpAcquirerId());
        entity.setGpIssuerId(dto.getGpIssuerId());
        entity.setTxnDate(dto.getTxnDate() != null ? java.sql.Date.valueOf(dto.getTxnDate()) : null);
        entity.setLocalTxnDateTime(dto.getLocalTxnDateTime());
        entity.setAmount(dto.getAmount());
        entity.setCurrency(dto.getCurrency());
        entity.setStatus(dto.getStatus());
        entity.setCardType(dto.getCardType());
        entity.setCardLast4(dto.getCardLast4());
        entity.setAuthCode(dto.getAuthCode());
        entity.setResponseCode(dto.getResponseCode());
        entity.setCreatedAt(dto.getCreatedAt());
        return entity;
    }
}
