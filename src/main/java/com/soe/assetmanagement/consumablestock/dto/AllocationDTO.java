package com.soe.assetmanagement.consumablestock.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO: Response cho DepartmentAllocation (Phân bổ hàng cho phòng ban)
 * CS-04: Departmental usage allocation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO cho phân bổ hàng (Allocation)")
public class AllocationDTO {
    
    @Schema(description = "ID phân bổ", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @Schema(description = "ID giao dịch xuất kho", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID stockTransactionId;
    
    @Schema(description = "ID phòng ban", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID departmentId;
    
    @Schema(description = "Tên phòng ban", example = "Phòng Hành chính")
    private String departmentName;
    
    @Schema(description = "Mã vật tư", example = "VT001")
    private String materialCode;
    
    @Schema(description = "Tên vật tư", example = "Giấy A4")
    private String materialName;
    
    @Schema(description = "Số lượng được cấp", example = "10")
    private Integer allocatedQuantity;
    
    @Schema(description = "Đơn vị tính", example = "ream")
    private String unit;
    
    @Schema(description = "Mã trung tâm chi phí", example = "CC-001")
    private String costCenterCode;
    
    @Schema(description = "Kỳ ngân sách", example = "Q1-2024")
    private String budgetPeriod;
    
    @Schema(description = "Thời gian cấp")
    private LocalDateTime allocatedAt;
    
    @Schema(description = "Ghi chú")
    private String notes;
}