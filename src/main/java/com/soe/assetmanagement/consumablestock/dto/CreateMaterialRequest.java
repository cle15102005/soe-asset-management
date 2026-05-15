package com.soe.assetmanagement.consumablestock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request tạo Material mới")
public class CreateMaterialRequest {
    
    @NotBlank(message = "Mã vật tư không được để trống")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Mã vật tư chỉ chứa chữ hoa, số, - và _")
    @Schema(description = "Mã vật tư (VD: VT001, XG-2024-001)", example = "VT001")
    private String materialCode;
    
    @NotBlank(message = "Tên vật tư không được để trống")
    @Schema(description = "Tên vật tư", example = "Giấy A4 80gsm")
    private String materialName;
    
    @Schema(description = "Phân loại", example = "OFFICE")
    private String category;
    
    @Schema(description = "Mô tả chi tiết")
    private String description;
    
    @Schema(description = "Thông số kỹ thuật")
    private String technicalSpecs;
    
    @NotBlank(message = "Đơn vị tính không được để trống")
    @Schema(description = "Đơn vị tính", example = "ream")
    private String unitOfMeasure;
    
    @Schema(description = "Nhà cung cấp")
    private String supplier;
}