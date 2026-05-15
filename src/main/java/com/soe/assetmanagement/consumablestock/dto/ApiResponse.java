package com.soe.assetmanagement.consumablestock.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API Response Wrapper
 * Dùng để bao quanh tất cả response từ Server
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Generic API Response wrapper")
public class ApiResponse<T> {
    
    @Schema(description = "HTTP Status code", example = "200")
    private int status;
    
    @Schema(description = "Thông báo", example = "Thành công")
    private String message;
    
    @Schema(description = "Dữ liệu trả về")
    private T data;
    
    @Schema(description = "Thời gian response")
    private LocalDateTime timestamp;
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> error(int status, String message) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}