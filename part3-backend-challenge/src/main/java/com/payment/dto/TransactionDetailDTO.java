package com.payment.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Transaction detail object")
public class TransactionDetailDTO {

    @Schema(description = "Detail ID", example = "123")
    private Long detailId;

    @Schema(description = "Detail type", example = "fee")
    private String type;

    @Schema(description = "Detail amount", example = "3.50")
    private BigDecimal amount;

    @Schema(description = "Detail description", example = "Processing fee")
    private String description;
}