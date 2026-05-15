package com.soe.assetmanagement.consumablestock.dto;

import java.time.LocalDate;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request ghi nhập kho")
public class ReceiptRequest {
    
    @NotNull(message = "ID vật tư không được null")
    @Schema(description = "ID vật tư", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID materialId;
    
    @NotNull(message = "Số lượng không được null")
    @Positive(message = "Số lượng phải > 0")
    @Schema(description = "Số lượng nhập", example = "50")
    private Integer quantity;
    
    @NotNull(message = "Ngày giao dịch không được null")
    @Schema(description = "Ngày nhập kho")
    private LocalDate transactionDate;
    
    @Schema(description = "Số tài liệu (hóa đơn)", example = "HD-2024-001")
    private String referenceDocNum;
    
    @Schema(description = "Ghi chú")
    private String notes;
}