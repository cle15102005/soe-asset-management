package com.soe.assetmanagement.consumablestock.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request cập nhật Material")
public class UpdateMaterialRequest {
    
    @Schema(description = "Tên vật tư mới")
    private String materialName;
    
    @Schema(description = "Phân loại mới")
    private String category;
    
    @Schema(description = "Mô tả chi tiết mới")
    private String description;
    
    @Schema(description = "Thông số kỹ thuật mới")
    private String technicalSpecs;
    
    @Schema(description = "Nhà cung cấp mới")
    private String supplier;
    
    @Schema(description = "Còn hoạt động?")
    private Boolean isActive;
}