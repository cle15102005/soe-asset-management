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

import com.soe.assetmanagement.consumablestock.dto.CreateMaterialRequest;
import com.soe.assetmanagement.consumablestock.dto.MaterialDTO;
import com.soe.assetmanagement.consumablestock.dto.ReceiptRequest;
import com.soe.assetmanagement.consumablestock.dto.StockBalanceDTO;
import com.soe.assetmanagement.consumablestock.repository.DepartmentAllocationRepository;
import com.soe.assetmanagement.consumablestock.repository.MaterialRepository;
import com.soe.assetmanagement.consumablestock.repository.StockBalanceRepository;
import com.soe.assetmanagement.consumablestock.repository.StockTransactionRepository;

/**
 * Unit Test cho StockBalanceService (CS-03)
 */
@SpringBootTest
@Transactional
class StockBalanceServiceTest {
    
    @Autowired
    private StockBalanceService balanceService;
    
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
    
    @BeforeEach
    void setUp() {
        allocationRepository.deleteAll();
        transactionRepository.deleteAll();
        balanceRepository.deleteAll();
        materialRepository.deleteAll();
        
        // Tạo vật tư test (sẽ auto-create StockBalance)
        CreateMaterialRequest req = CreateMaterialRequest.builder()
                .materialCode("VT001")
                .materialName("Giấy A4")
                .unitOfMeasure("ream")
                .build();
        MaterialDTO material = materialService.create(req, userId);
        materialId = material.getId();
    }
    
    @Test
    void testGetCurrentBalance() {
        // Arrange: Nhập 50
        ReceiptRequest receipt = ReceiptRequest.builder()
                .materialId(materialId)
                .quantity(50)
                .transactionDate(LocalDate.now())
                .build();
        transactionService.createReceipt(receipt, userId);
        
        // Act
        Integer balance = balanceService.getCurrentBalance(materialId);
        
        // Assert
        assertEquals(50, balance);
    }
    
    @Test
    void testGetBalance() {
        // Arrange
        ReceiptRequest receipt = ReceiptRequest.builder()
                .materialId(materialId)
                .quantity(100)
                .transactionDate(LocalDate.now())
                .build();
        transactionService.createReceipt(receipt, userId);
        
        // Act
        StockBalanceDTO result = balanceService.getBalance(materialId);
        
        // Assert
        assertNotNull(result);
        assertEquals(100, result.getCurrentQuantity());
    }
    
    @Test
    void testGetLowStockItems() {
        // Arrange: Nhập 10, set min level = 20
        ReceiptRequest receipt = ReceiptRequest.builder()
                .materialId(materialId)
                .quantity(10)
                .transactionDate(LocalDate.now())
                .build();
        transactionService.createReceipt(receipt, userId);
        balanceService.setMinReorderLevel(materialId, 20);
        
        // Act
        List<StockBalanceDTO> result = balanceService.getLowStockItems();
        
        // Assert
        assertTrue(result.size() > 0);
        assertTrue(result.get(0).getNeedsReorder());
    }
    
    @Test
    void testSetMinReorderLevel() {
        // Arrange
        ReceiptRequest receipt = ReceiptRequest.builder()
                .materialId(materialId)
                .quantity(50)
                .transactionDate(LocalDate.now())
                .build();
        transactionService.createReceipt(receipt, userId);
        
        // Act
        StockBalanceDTO result = balanceService.setMinReorderLevel(materialId, 30);
        
        // Assert
        assertEquals(30, result.getMinReorderLevel());
    }
    
    @Test
    void testGetAllBalances() {
        // Arrange
        ReceiptRequest receipt = ReceiptRequest.builder()
                .materialId(materialId)
                .quantity(50)
                .transactionDate(LocalDate.now())
                .build();
        transactionService.createReceipt(receipt, userId);
        
        // Act
        List<StockBalanceDTO> result = balanceService.getAllBalances();
        
        // Assert
        assertTrue(result.size() > 0);
    }
}