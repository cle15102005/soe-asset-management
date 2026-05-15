package com.soe.assetmanagement.consumablestock.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.soe.assetmanagement.consumablestock.dto.AllocationDTO;
import com.soe.assetmanagement.consumablestock.dto.CreateMaterialRequest;
import com.soe.assetmanagement.consumablestock.dto.IssueRequest;
import com.soe.assetmanagement.consumablestock.dto.MaterialDTO;
import com.soe.assetmanagement.consumablestock.dto.ReceiptRequest;
import com.soe.assetmanagement.consumablestock.repository.DepartmentAllocationRepository;
import com.soe.assetmanagement.consumablestock.repository.MaterialRepository;
import com.soe.assetmanagement.consumablestock.repository.StockBalanceRepository;
import com.soe.assetmanagement.consumablestock.repository.StockTransactionRepository;

/**
 * Unit Test cho AllocationService (CS-04)
 */
@SpringBootTest
@Transactional
class AllocationServiceTest {
    
    @Autowired
    private AllocationService allocationService;
    
    @Autowired
    private MaterialService materialService;
    
    @Autowired
    private StockTransactionService transactionService;
    
    @Autowired
    private MaterialRepository materialRepository;
    
    @Autowired
    private StockTransactionRepository transactionRepository;
    
    @Autowired
    private StockBalanceRepository balanceRepository;
    
    @Autowired
    private DepartmentAllocationRepository allocationRepository;
    
    private UUID userId = UUID.randomUUID();
    private UUID materialId;
    private UUID deptId = UUID.randomUUID();
    
    @BeforeEach
    void setUp() {
        allocationRepository.deleteAll();
        transactionRepository.deleteAll();
        balanceRepository.deleteAll();
        materialRepository.deleteAll();
        
        // Tạo vật tư + nhập 100
        CreateMaterialRequest req = CreateMaterialRequest.builder()
                .materialCode("VT001")
                .materialName("Giấy A4")
                .unitOfMeasure("ream")
                .build();
        MaterialDTO material = materialService.create(req, userId);
        materialId = material.getId();
        
        ReceiptRequest receipt = ReceiptRequest.builder()
                .materialId(materialId)
                .quantity(100)
                .transactionDate(LocalDate.now())
                .build();
        transactionService.createReceipt(receipt, userId);
    }
    
    @Test
    void testAllocateToDepartment() {
        // Arrange
        IssueRequest request = IssueRequest.builder()
                .materialId(materialId)
                .quantity(20)
                .departmentId(deptId)
                .departmentName("Phòng Hành chính")
                .transactionDate(LocalDate.now())
                .budgetPeriod("Q1-2024")
                .build();
        
        // Act
        AllocationDTO result = allocationService.allocateToDepartment(request, userId);
        
        // Assert
        assertNotNull(result);
        assertEquals(20, result.getAllocatedQuantity());
        assertEquals(deptId, result.getDepartmentId());
    }
    
    @Test
    void testGetByDepartmentId() {
        // Arrange: Allocate 2 lần
        IssueRequest req1 = IssueRequest.builder()
                .materialId(materialId)
                .quantity(10)
                .departmentId(deptId)
                .departmentName("Phòng Hành chính")
                .transactionDate(LocalDate.now())
                .budgetPeriod("Q1-2024")
                .build();
        
        IssueRequest req2 = IssueRequest.builder()
                .materialId(materialId)
                .quantity(15)
                .departmentId(deptId)
                .departmentName("Phòng Hành chính")
                .transactionDate(LocalDate.now())
                .budgetPeriod("Q1-2024")
                .build();
        
        allocationService.allocateToDepartment(req1, userId);
        allocationService.allocateToDepartment(req2, userId);
        
        // Act
        List<AllocationDTO> result = allocationService.getByDepartmentId(deptId);
        
        // Assert
        assertEquals(2, result.size());
    }
    
    @Test
    void testGetByBudgetPeriod() {
        // Arrange
        IssueRequest request = IssueRequest.builder()
                .materialId(materialId)
                .quantity(20)
                .departmentId(deptId)
                .departmentName("Phòng Hành chính")
                .transactionDate(LocalDate.now())
                .budgetPeriod("Q1-2024")
                .build();
        allocationService.allocateToDepartment(request, userId);
        
        // Act
        List<AllocationDTO> result = allocationService.getByBudgetPeriod("Q1-2024");
        
        // Assert
        assertTrue(result.size() > 0);
    }
}