package com.soe.assetmanagement.consumablestock.controller;

import com.soe.assetmanagement.consumablestock.dto.ApiResponse;
import com.soe.assetmanagement.consumablestock.dto.IssueRequest;
import com.soe.assetmanagement.consumablestock.dto.ReceiptRequest;
import com.soe.assetmanagement.consumablestock.dto.StockTransactionDTO;
import com.soe.assetmanagement.consumablestock.service.StockTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Controller: StockTransactionController
 * REST API cho CS-02: Stock receipt & issue tracking
 * Base path: /api/stocks
 */
@RestController
@RequestMapping("/api/stocks")
@Tag(name = "Stock Transactions", description = "Quản lý ghi nhập/xuất kho (CS-02)")
@Slf4j
public class StockTransactionController {
    
    @Autowired
    private StockTransactionService transactionService;
    
    /**
     * POST /api/stocks/receipts
     * Ghi nhập kho
     */
    @PostMapping("/receipts")
    @Operation(summary = "Ghi nhập kho")
    public ResponseEntity<ApiResponse<StockTransactionDTO>> createReceipt(
            @Valid @RequestBody ReceiptRequest request) {
        log.info("POST /api/stocks/receipts - Creating receipt for material: {}", request.getMaterialId());
        
        // TODO: Lấy userId từ JWT token
        UUID userId = UUID.randomUUID();
        
        StockTransactionDTO transaction = transactionService.createReceipt(request, userId);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(transaction, "Ghi nhập kho thành công"));
    }
    
    /**
     * POST /api/stocks/issues
     * Ghi xuất kho
     */
    @PostMapping("/issues")
    @Operation(summary = "Ghi xuất kho")
    public ResponseEntity<ApiResponse<StockTransactionDTO>> createIssue(
            @Valid @RequestBody IssueRequest request) {
        log.info("POST /api/stocks/issues - Creating issue for material: {}", request.getMaterialId());
        
        // TODO: Lấy userId từ JWT token
        UUID userId = UUID.randomUUID();
        
        StockTransactionDTO transaction = transactionService.createIssue(request, userId);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(transaction, "Ghi xuất kho thành công"));
    }
    
    /**
     * GET /api/stocks/transactions/{materialId}
     * Lấy tất cả giao dịch của 1 vật tư
     */
    @GetMapping("/transactions/{materialId}")
    @Operation(summary = "Lấy lịch sử giao dịch của vật tư")
    public ResponseEntity<ApiResponse<List<StockTransactionDTO>>> getByMaterialId(
            @PathVariable UUID materialId) {
        log.info("GET /api/stocks/transactions/{} - Fetching transactions", materialId);
        
        List<StockTransactionDTO> transactions = transactionService.getByMaterialId(materialId);
        
        return ResponseEntity.ok(
                ApiResponse.success(transactions, "Lấy lịch sử giao dịch thành công")
        );
    }
    
    /**
     * GET /api/stocks/transactions/{materialId}?startDate=2024-01-01&endDate=2024-12-31
     * Lấy giao dịch trong khoảng thời gian
     */
    @GetMapping("/transactions/{materialId}/range")
    @Operation(summary = "Lấy giao dịch trong khoảng thời gian")
    public ResponseEntity<ApiResponse<List<StockTransactionDTO>>> getByDateRange(
            @PathVariable UUID materialId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("GET /api/stocks/transactions/{}/range - Date range: {} to {}", 
                materialId, startDate, endDate);
        
        List<StockTransactionDTO> transactions = transactionService.getByDateRange(materialId, startDate, endDate);
        
        return ResponseEntity.ok(
                ApiResponse.success(transactions, "Lấy giao dịch theo khoảng thời gian thành công")
        );
    }
    
    /**
     * GET /api/stocks/transactions/{id}
     * Lấy chi tiết 1 giao dịch
     */
    @GetMapping("/transactions/{id}")
    @Operation(summary = "Lấy chi tiết giao dịch")
    public ResponseEntity<ApiResponse<StockTransactionDTO>> getById(
            @PathVariable UUID id) {
        log.info("GET /api/stocks/transactions/{} - Fetching transaction", id);
        
        StockTransactionDTO transaction = transactionService.getById(id);
        
        return ResponseEntity.ok(
                ApiResponse.success(transaction, "Lấy chi tiết giao dịch thành công")
        );
    }
}