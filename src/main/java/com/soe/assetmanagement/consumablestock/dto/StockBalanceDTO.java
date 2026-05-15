package com.soe.assetmanagement.consumablestock.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO cho StockBalance (Tồn kho hiện tại)")
public class StockBalanceDTO {
    
    @Schema(description = "ID")
    private UUID id;
    
    @Schema(description = "ID vật tư")
    private UUID materialId;
    
    @Schema(description = "Mã vật tư")
    private String materialCode;
    
    @Schema(description = "Tên vật tư")
    private String materialName;
    
    @Schema(description = "Số lượng hiện tại tồn kho", example = "45")
    private Integer currentQuantity;
    
    @Schema(description = "Đơn vị tính", example = "ream")
    private String unit;
    
    @Schema(description = "Ngày nhập cuối cùng")
    private LocalDate lastReceiptDate;
    
    @Schema(description = "Ngày xuất cuối cùng")
    private LocalDate lastIssueDate;
    
    @Schema(description = "Mức tối thiểu cần nhập lại", example = "20")
    private Integer minReorderLevel;
    
    @Schema(description = "Cần nhập lại?", example = "false")
    private Boolean needsReorder;
    
    @Schema(description = "Thời gian cập nhật lần cuối")
    private LocalDateTime updatedAt;
}