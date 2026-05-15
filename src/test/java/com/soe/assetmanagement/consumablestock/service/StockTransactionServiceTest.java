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
import com.soe.assetmanagement.consumablestock.dto.IssueRequest;
import com.soe.assetmanagement.consumablestock.dto.MaterialDTO;
import com.soe.assetmanagement.consumablestock.dto.ReceiptRequest;
import com.soe.assetmanagement.consumablestock.dto.StockTransactionDTO;
import com.soe.assetmanagement.consumablestock.entity.StockTransaction;
import com.soe.assetmanagement.consumablestock.exception.InsufficientStockException;
import com.soe.assetmanagement.consumablestock.repository.DepartmentAllocationRepository;
import com.soe.assetmanagement.consumablestock.repository.MaterialRepository;
import com.soe.assetmanagement.consumablestock.repository.StockBalanceRepository;
import com.soe.assetmanagement.consumablestock.repository.StockTransactionRepository;

/**
 * Unit Test cho StockTransactionService (CS-02)
 */
@SpringBootTest
@Transactional
class StockTransactionServiceTest {
    
    @Autowired
    private StockTransactionService transactionService;
    
    @Autowired
    private MaterialService materialService;
    
    @Autowired
    private StockBalanceService balanceService;
    
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
        
        // Tạo vật tư test
        CreateMaterialRequest req = CreateMaterialRequest.builder()
                .materialCode("VT001")
                .materialName("Giấy A4")
                .unitOfMeasure("ream")
                .build();
        MaterialDTO material = materialService.create(req, userId);
        materialId = material.getId();
    }
    
    @Test
    void testCreateReceiptSuccess() {
        // Arrange
        ReceiptRequest request = ReceiptRequest.builder()
                .materialId(materialId)
                .quantity(50)
                .transactionDate(LocalDate.now())
                .referenceDocNum("HD-2024-001")
                .notes("Nhập từ nhà cung cấp A")
                .build();
        
        // Act
        StockTransactionDTO result = transactionService.createReceipt(request, userId);
        
        // Assert
        assertNotNull(result);
        assertEquals(StockTransaction.TransactionType.RECEIPT, result.getTransactionType());
        assertEquals(50, result.getQuantity());
    }
    
    @Test
    void testCreateIssueSuccess() {
        // Arrange: Nhập 50 ream trước
        ReceiptRequest receipt = ReceiptRequest.builder()
                .materialId(materialId)
                .quantity(50)
                .transactionDate(LocalDate.now())
                .build();
        transactionService.createReceipt(receipt, userId);
        
        // Act: Xuất 10 ream
        IssueRequest issue = IssueRequest.builder()
                .materialId(materialId)
                .quantity(10)
                .departmentId(UUID.randomUUID())
                .transactionDate(LocalDate.now())
                .budgetPeriod("Q1-2024")
                .build();
        StockTransactionDTO result = transactionService.createIssue(issue, userId);
        
        // Assert
        assertNotNull(result);
        assertEquals(StockTransaction.TransactionType.ISSUE, result.getTransactionType());
        assertEquals(10, result.getQuantity());
    }
    
    @Test
    void testCreateIssueWithInsufficientStockThrowsException() {
        // Arrange: Chỉ nhập 50 ream
        ReceiptRequest receipt = ReceiptRequest.builder()
                .materialId(materialId)
                .quantity(50)
                .transactionDate(LocalDate.now())
                .build();
        transactionService.createReceipt(receipt, userId);
        
        // Act & Assert: Cố xuất 100 ream → Exception
        IssueRequest issue = IssueRequest.builder()
                .materialId(materialId)
                .quantity(100)
                .departmentId(UUID.randomUUID())
                .transactionDate(LocalDate.now())
                .build();
        
        assertThrows(InsufficientStockException.class, () -> {
            transactionService.createIssue(issue, userId);
        });
    }
    
    @Test
    void testGetTransactionsByMaterialId() {
        // Arrange: Tạo 3 giao dịch
        ReceiptRequest req1 = ReceiptRequest.builder()
                .materialId(materialId)
                .quantity(30)
                .transactionDate(LocalDate.now().minusDays(2))
                .build();
        ReceiptRequest req2 = ReceiptRequest.builder()
                .materialId(materialId)
                .quantity(20)
                .transactionDate(LocalDate.now().minusDays(1))
                .build();
        transactionService.createReceipt(req1, userId);
        transactionService.createReceipt(req2, userId);
        
        IssueRequest issue = IssueRequest.builder()
                .materialId(materialId)
                .quantity(10)
                .departmentId(UUID.randomUUID())
                .transactionDate(LocalDate.now())
                .build();
        transactionService.createIssue(issue, userId);
        
        // Act
        List<StockTransactionDTO> result = transactionService.getByMaterialId(materialId);
        
        // Assert: Phải có 3 giao dịch
        assertEquals(3, result.size());
    }
    
    @Test
    void testStockBalanceCalculatedCorrectly() {
        // Arrange & Act
        ReceiptRequest receipt = ReceiptRequest.builder()
                .materialId(materialId)
                .quantity(50)
                .transactionDate(LocalDate.now())
                .build();
        transactionService.createReceipt(receipt, userId);
        
        IssueRequest issue = IssueRequest.builder()
                .materialId(materialId)
                .quantity(15)
                .departmentId(UUID.randomUUID())
                .transactionDate(LocalDate.now())
                .build();
        transactionService.createIssue(issue, userId);
        
        // Assert: Balance = 50 - 15 = 35
        Integer balance = balanceService.getCurrentBalance(materialId);
        assertEquals(35, balance);
    }
    
    @Test
    void testIssueCannotExceedCurrentStock() {
        // Arrange
        ReceiptRequest receipt = ReceiptRequest.builder()
                .materialId(materialId)
                .quantity(20)
                .transactionDate(LocalDate.now())
                .build();
        transactionService.createReceipt(receipt, userId);
        
        // Đã xuất 10
        IssueRequest issue1 = IssueRequest.builder()
                .materialId(materialId)
                .quantity(10)
                .departmentId(UUID.randomUUID())
                .transactionDate(LocalDate.now())
                .build();
        transactionService.createIssue(issue1, userId);
        
        // Cố xuất 15 (chỉ còn 10) → Exception
        IssueRequest issue2 = IssueRequest.builder()
                .materialId(materialId)
                .quantity(15)
                .departmentId(UUID.randomUUID())
                .transactionDate(LocalDate.now())
                .build();
        
        assertThrows(InsufficientStockException.class, () -> {
            transactionService.createIssue(issue2, userId);
        });
    }
    
    @Test
    void testGetTransactionsByDateRange() {
        // Arrange
        LocalDate today = LocalDate.now();
        
        ReceiptRequest req1 = ReceiptRequest.builder()
                .materialId(materialId)
                .quantity(30)
                .transactionDate(today.minusDays(5))
                .build();
        
        ReceiptRequest req2 = ReceiptRequest.builder()
                .materialId(materialId)
                .quantity(20)
                .transactionDate(today)
                .build();
        
        transactionService.createReceipt(req1, userId);
        transactionService.createReceipt(req2, userId);
        
        // Act: Lấy giao dịch 3 ngày gần đây
        List<StockTransactionDTO> result = transactionService
                .getByDateRange(materialId, today.minusDays(3), today);
        
        // Assert: Chỉ có 1 giao dịch (req2)
        assertEquals(1, result.size());
    }
    
    @Test
    void testBalanceWithMultipleTransactions() {
        // Nhập 100
        ReceiptRequest receipt1 = ReceiptRequest.builder()
                .materialId(materialId)
                .quantity(100)
                .transactionDate(LocalDate.now())
                .build();
        transactionService.createReceipt(receipt1, userId);
        
        // Xuất 20
        IssueRequest issue1 = IssueRequest.builder()
                .materialId(materialId)
                .quantity(20)
                .departmentId(UUID.randomUUID())
                .transactionDate(LocalDate.now())
                .build();
        transactionService.createIssue(issue1, userId);
        
        // Nhập thêm 50
        ReceiptRequest receipt2 = ReceiptRequest.builder()
                .materialId(materialId)
                .quantity(50)
                .transactionDate(LocalDate.now())
                .build();
        transactionService.createReceipt(receipt2, userId);
        
        // Xuất 30
        IssueRequest issue2 = IssueRequest.builder()
                .materialId(materialId)
                .quantity(30)
                .departmentId(UUID.randomUUID())
                .transactionDate(LocalDate.now())
                .build();
        transactionService.createIssue(issue2, userId);
        
        // Assert: Balance = 100 - 20 + 50 - 30 = 100
        Integer balance = balanceService.getCurrentBalance(materialId);
        assertEquals(100, balance);
    }
}