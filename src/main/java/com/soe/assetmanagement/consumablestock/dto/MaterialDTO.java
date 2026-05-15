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
@Schema(description = "DTO cho Material (Vật tư)")
public class MaterialDTO {
    
    @Schema(description = "ID vật tư", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    
    @Schema(description = "Mã vật tư", example = "VT001")
    private String materialCode;
    
    @Schema(description = "Tên vật tư", example = "Giấy A4 80gsm")
    private String materialName;
    
    @Schema(description = "Phân loại", example = "OFFICE")
    private String category;
    
    @Schema(description = "Mô tả chi tiết")
    private String description;
    
    @Schema(description = "Thông số kỹ thuật", example = "80gsm, định mức A4")
    private String technicalSpecs;
    
    @Schema(description = "Đơn vị tính", example = "ream")
    private String unitOfMeasure;
    
    @Schema(description = "Nhà cung cấp", example = "Công ty XYZ")
    private String supplier;
    
    @Schema(description = "Thời gian tạo")
    private LocalDateTime createdAt;
    
    @Schema(description = "Thời gian cập nhật lần cuối")
    private LocalDateTime updatedAt;
    
    @Schema(description = "Là hoạt động?", example = "true")
    private Boolean isActive;
}