package com.soe.assetmanagement.consumablestock.service;

import com.soe.assetmanagement.consumablestock.dto.AllocationDTO;
import com.soe.assetmanagement.consumablestock.dto.ConsumptionReportDTO;
import com.soe.assetmanagement.consumablestock.dto.IssueRequest;
import com.soe.assetmanagement.consumablestock.entity.DepartmentAllocation;
import com.soe.assetmanagement.consumablestock.entity.StockTransaction;
import com.soe.assetmanagement.consumablestock.exception.ResourceNotFoundException;
import com.soe.assetmanagement.consumablestock.repository.DepartmentAllocationRepository;
import com.soe.assetmanagement.consumablestock.repository.StockTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.soe.assetmanagement.consumablestock.dto.StockTransactionDTO;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service: AllocationService
 * Business logic cho CS-04: Departmental usage allocation
 */
@Service
@Slf4j
@Transactional
public class AllocationService {
    
    @Autowired
    private DepartmentAllocationRepository allocationRepository;
    
    @Autowired
    private StockTransactionRepository transactionRepository;
    
    @Autowired
    private StockTransactionService transactionService;
    
    /**
     * Cấp hàng cho phòng ban + tạo allocation
     */
    public AllocationDTO allocateToDepartment(IssueRequest request, UUID userId) {
        log.info("Allocating material to department: {}, quantity: {}", request.getDepartmentId(), request.getQuantity());
        
        // Bước 1: Tạo stock transaction (ISSUE)
        StockTransactionDTO transactionDTO = transactionService.createIssue(request, userId);
        
        // Bước 2: Tạo allocation record
        StockTransaction transaction = transactionRepository.findById(transactionDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Giao dịch không tồn tại"));
        
        DepartmentAllocation allocation = DepartmentAllocation.builder()
                .stockTransaction(transaction)
                .departmentId(request.getDepartmentId())
                .departmentName(request.getDepartmentName())
                .allocatedQuantity(request.getQuantity())
                .costCenterCode(request.getCostCenterCode())
                .budgetPeriod(request.getBudgetPeriod())
                .createdBy(userId)
                .notes(request.getNotes())
                .build();
        
        DepartmentAllocation saved = allocationRepository.save(allocation);
        log.info("Allocation created successfully with ID: {}", saved.getId());
        
        return convertToDTO(saved);
    }
    
    /**
     * Lấy danh sách phân bổ của 1 phòng
     */
    @Transactional(readOnly = true)
    public List<AllocationDTO> getByDepartmentId(UUID departmentId) {
        return allocationRepository.findByDepartmentId(departmentId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy danh sách phân bổ theo kỳ
     */
    @Transactional(readOnly = true)
    public List<AllocationDTO> getByBudgetPeriod(String budgetPeriod) {
        return allocationRepository.findByBudgetPeriod(budgetPeriod)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Báo cáo tiêu thụ của phòng trong kỳ
     */
    @Transactional(readOnly = true)
    public ConsumptionReportDTO getConsumptionReport(UUID departmentId, String budgetPeriod) {
        List<DepartmentAllocation> allocations = allocationRepository
                .findByDepartmentAndPeriod(departmentId, budgetPeriod);
        
        if (allocations.isEmpty()) {
            throw new ResourceNotFoundException("Không có dữ liệu tiêu thụ cho phòng và kỳ này");
        }
        
        // Tính tổng
        Integer totalConsumed = allocations.stream()
                .mapToInt(DepartmentAllocation::getAllocatedQuantity)
                .sum();
        
        DepartmentAllocation first = allocations.get(0);
        
        return ConsumptionReportDTO.builder()
                .departmentId(departmentId)
                .departmentName(first.getDepartmentName())
                .budgetPeriod(budgetPeriod)
                .totalConsumed(totalConsumed)
                .allocationCount((long) allocations.size())
                .lastAllocationDate(allocations.get(0).getAllocatedAt())
                .build();
    }
    
    /**
     * Báo cáo tiêu thụ chi tiết theo phòng & kỳ
     */
    @Transactional(readOnly = true)
    public List<ConsumptionReportDTO> getDetailedConsumptionReport(UUID departmentId, String budgetPeriod) {
        return allocationRepository.findByDepartmentAndPeriod(departmentId, budgetPeriod)
                .stream()
                .map(allocation -> ConsumptionReportDTO.builder()
                        .departmentId(allocation.getDepartmentId())
                        .departmentName(allocation.getDepartmentName())
                        .budgetPeriod(allocation.getBudgetPeriod())
                        .materialCode(allocation.getStockTransaction().getMaterial().getMaterialCode())
                        .materialName(allocation.getStockTransaction().getMaterial().getMaterialName())
                        .unitOfMeasure(allocation.getStockTransaction().getUnit())
                        .totalConsumed(allocation.getAllocatedQuantity())
                        .lastAllocationDate(allocation.getAllocatedAt())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * Convert to DTO
     */
    private AllocationDTO convertToDTO(DepartmentAllocation allocation) {
        StockTransaction transaction = allocation.getStockTransaction();
        
        return AllocationDTO.builder()
                .id(allocation.getId())
                .stockTransactionId(transaction.getId())
                .departmentId(allocation.getDepartmentId())
                .departmentName(allocation.getDepartmentName())
                .materialCode(transaction.getMaterial().getMaterialCode())
                .materialName(transaction.getMaterial().getMaterialName())
                .allocatedQuantity(allocation.getAllocatedQuantity())
                .unit(transaction.getUnit())
                .costCenterCode(allocation.getCostCenterCode())
                .budgetPeriod(allocation.getBudgetPeriod())
                .allocatedAt(allocation.getAllocatedAt())
                .notes(allocation.getNotes())
                .build();
    }
}