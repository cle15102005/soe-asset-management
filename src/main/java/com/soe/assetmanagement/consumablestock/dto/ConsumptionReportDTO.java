package com.soe.assetmanagement.consumablestock.dto;

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
@Schema(description = "DTO cho báo cáo tiêu thụ theo phòng ban")
public class ConsumptionReportDTO {
    
    @Schema(description = "ID phòng ban")
    private UUID departmentId;
    
    @Schema(description = "Tên phòng ban")
    private String departmentName;
    
    @Schema(description = "Kỳ ngân sách", example = "Q1-2024")
    private String budgetPeriod;
    
    @Schema(description = "Mã vật tư")
    private String materialCode;
    
    @Schema(description = "Tên vật tư")
    private String materialName;
    
    @Schema(description = "Đơn vị tính")
    private String unitOfMeasure;
    
    @Schema(description = "Tổng số lượng đã tiêu thụ", example = "150")
    private Integer totalConsumed;
    
    @Schema(description = "Số lần cấp")
    private Long allocationCount;
    
    @Schema(description = "Lần cấp cuối cùng")
    private LocalDateTime lastAllocationDate;
}