package com.soe.assetmanagement.consumablestock.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.soe.assetmanagement.consumablestock.entity.StockTransaction;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO cho StockTransaction (Giao dịch tồn kho)")
public class StockTransactionDTO {
    
    @Schema(description = "ID giao dịch")
    private UUID id;
    
    @Schema(description = "ID vật tư")
    private UUID materialId;
    
    @Schema(description = "Mã vật tư", example = "VT001")
    private String materialCode;
    
    @Schema(description = "Tên vật tư")
    private String materialName;
    
    @Schema(description = "Loại giao dịch", example = "RECEIPT")
    private StockTransaction.TransactionType transactionType;
    
    @Schema(description = "Số lượng", example = "20")
    private Integer quantity;
    
    @Schema(description = "Đơn vị", example = "ream")
    private String unit;
    
    @Schema(description = "Ngày giao dịch")
    private LocalDate transactionDate;
    
    @Schema(description = "Số tài liệu", example = "HD-2024-001")
    private String referenceDocNum;
    
    @Schema(description = "Ghi chú")
    private String notes;
    
    @Schema(description = "Thời gian tạo")
    private LocalDateTime createdAt;
}