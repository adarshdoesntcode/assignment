package com.payment.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Pagination information")
public class PaginationDTO {

    @Schema(description = "Current page number", example = "0")
    private Integer page;

    @Schema(description = "Page size", example = "20")
    private Integer size;

    @Schema(description = "Total number of pages", example = "77")
    private Integer totalPages;

    @Schema(description = "Total number of elements", example = "1523")
    private Long totalElements;
}