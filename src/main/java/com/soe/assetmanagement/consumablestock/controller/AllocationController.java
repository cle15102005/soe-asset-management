package com.soe.assetmanagement.consumablestock.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soe.assetmanagement.consumablestock.dto.AllocationDTO;
import com.soe.assetmanagement.consumablestock.dto.ApiResponse;
import com.soe.assetmanagement.consumablestock.dto.ConsumptionReportDTO;
import com.soe.assetmanagement.consumablestock.dto.IssueRequest;
import com.soe.assetmanagement.consumablestock.service.AllocationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller: AllocationController
 * REST API cho CS-04: Departmental usage allocation
 * Base path: /api/allocations
 */
@RestController
@RequestMapping("/api/allocations")
@Tag(name = "Allocations", description = "Phân bổ hàng cho phòng ban (CS-04)")
@Slf4j
public class AllocationController {
    
    @Autowired
    private AllocationService allocationService;
    
    /**
     * POST /api/allocations
     * Cấp hàng cho phòng ban (ghi xuất + allocation)
     */
    @PostMapping
    @Operation(summary = "Cấp hàng cho phòng ban")
    public ResponseEntity<ApiResponse<AllocationDTO>> allocate(
            @Valid @RequestBody IssueRequest request) {
        log.info("POST /api/allocations - Allocating to department: {}", request.getDepartmentId());
        
        // TODO: Lấy userId từ JWT token
        java.util.UUID userId = java.util.UUID.randomUUID();
        
        AllocationDTO allocation = allocationService.allocateToDepartment(request, userId);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(allocation, "Cấp hàng cho phòng ban thành công"));
    }
    
    /**
     * GET /api/allocations/department/{departmentId}
     * Lấy tất cả phân bổ của 1 phòng
     */
    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Lấy lịch sử cấp hàng của phòng ban")
    public ResponseEntity<ApiResponse<List<AllocationDTO>>> getByDepartmentId(
            @PathVariable UUID departmentId) {
        log.info("GET /api/allocations/department/{} - Fetching allocations", departmentId);
        
        List<AllocationDTO> allocations = allocationService.getByDepartmentId(departmentId);
        
        return ResponseEntity.ok(
                ApiResponse.success(allocations, "Lấy lịch sử cấp hàng thành công")
        );
    }
    
    /**
     * GET /api/allocations/period/{budgetPeriod}
     * Lấy phân bổ theo kỳ
     */
    @GetMapping("/period/{budgetPeriod}")
    @Operation(summary = "Lấy phân bổ theo kỳ ngân sách")
    public ResponseEntity<ApiResponse<List<AllocationDTO>>> getByBudgetPeriod(
            @PathVariable String budgetPeriod) {
        log.info("GET /api/allocations/period/{} - Fetching allocations", budgetPeriod);
        
        List<AllocationDTO> allocations = allocationService.getByBudgetPeriod(budgetPeriod);
        
        return ResponseEntity.ok(
                ApiResponse.success(allocations, "Lấy phân bổ theo kỳ thành công")
        );
    }
    
    /**
     * GET /api/allocations/department/{departmentId}/period/{budgetPeriod}
     * Báo cáo tiêu thụ của 1 phòng trong 1 kỳ
     */
    @GetMapping("/department/{departmentId}/period/{budgetPeriod}")
    @Operation(summary = "Báo cáo tiêu thụ tổng hợp của phòng")
    public ResponseEntity<ApiResponse<ConsumptionReportDTO>> getConsumptionReport(
            @PathVariable UUID departmentId,
            @PathVariable String budgetPeriod) {
        log.info("GET /api/allocations/department/{}/period/{} - Fetching consumption report", 
                departmentId, budgetPeriod);
        
        ConsumptionReportDTO report = allocationService.getConsumptionReport(departmentId, budgetPeriod);
        
        return ResponseEntity.ok(
                ApiResponse.success(report, "Lấy báo cáo tiêu thụ thành công")
        );
    }
    
    /**
     * GET /api/allocations/department/{departmentId}/period/{budgetPeriod}/detail
     * Báo cáo tiêu thụ chi tiết (từng loại vật tư)
     */
    @GetMapping("/department/{departmentId}/period/{budgetPeriod}/detail")
    @Operation(summary = "Báo cáo tiêu thụ chi tiết theo loại vật tư")
    public ResponseEntity<ApiResponse<List<ConsumptionReportDTO>>> getDetailedConsumptionReport(
            @PathVariable UUID departmentId,
            @PathVariable String budgetPeriod) {
        log.info("GET /api/allocations/department/{}/period/{}/detail - Fetching detailed report", 
                departmentId, budgetPeriod);
        
        List<ConsumptionReportDTO> report = allocationService.getDetailedConsumptionReport(departmentId, budgetPeriod);
        
        return ResponseEntity.ok(
                ApiResponse.success(report, "Lấy báo cáo chi tiết thành công")
        );
    }
}