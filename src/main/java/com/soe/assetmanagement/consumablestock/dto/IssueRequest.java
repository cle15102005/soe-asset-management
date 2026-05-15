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
@Schema(description = "Request ghi xuất kho")
public class IssueRequest {
    
    @NotNull(message = "ID vật tư không được null")
    @Schema(description = "ID vật tư", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID materialId;
    
    @NotNull(message = "Số lượng không được null")
    @Positive(message = "Số lượng phải > 0")
    @Schema(description = "Số lượng xuất", example = "10")
    private Integer quantity;
    
    @NotNull(message = "Phòng ban không được null")
    @Schema(description = "ID phòng ban nhận", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID departmentId;
    
    @Schema(description = "Tên phòng ban", example = "Phòng Hành chính")
    private String departmentName;
    
    @NotNull(message = "Ngày giao dịch không được null")
    @Schema(description = "Ngày xuất kho")
    private LocalDate transactionDate;
    
    @Schema(description = "Mã trung tâm chi phí", example = "CC-001")
    private String costCenterCode;
    
    @Schema(description = "Kỳ ngân sách", example = "Q1-2024")
    private String budgetPeriod;
    
    @Schema(description = "Ghi chú")
    private String notes;
}