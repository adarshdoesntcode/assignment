package com.payment.dto.merchant;

import com.payment.dto.common.PaginationDTO;
import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Serdeable
@Schema(description = "Response object for merchant list with pagination")
public class MerchantListResponse {

    @Schema(description = "List of merchants")
    private List<MerchantResponse> merchants;

    @Schema(description = "Pagination information")
    private PaginationDTO pagination;
}
