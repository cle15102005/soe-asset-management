package com.soe.assetmanagement.consumablestock.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soe.assetmanagement.consumablestock.dto.ApiResponse;
import com.soe.assetmanagement.consumablestock.dto.StockBalanceDTO;
import com.soe.assetmanagement.consumablestock.service.StockBalanceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller: StockBalanceController
 * REST API cho CS-03: Real-time stock balance
 * Base path: /api/balance
 */
@RestController
@RequestMapping("/api/balance")
@Tag(name = "Stock Balance", description = "Tồn kho thực tế (CS-03)")
@Slf4j
public class StockBalanceController {
    
    @Autowired
    private StockBalanceService balanceService;
    
    /**
     * GET /api/balance/{materialId}
     * Lấy tồn kho hiện tại
     */
    @GetMapping("/{materialId}")
    @Operation(summary = "Lấy số lượng tồn kho")
    public ResponseEntity<ApiResponse<StockBalanceDTO>> getBalance(
            @PathVariable UUID materialId) {
        log.info("GET /api/balance/{} - Fetching balance", materialId);
        
        StockBalanceDTO balance = balanceService.getBalance(materialId);
        
        return ResponseEntity.ok(
                ApiResponse.success(balance, "Lấy tồn kho thành công")
        );
    }
    
    /**
     * GET /api/balance
     * Lấy tất cả tồn kho
     */
    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả tồn kho")
    public ResponseEntity<ApiResponse<List<StockBalanceDTO>>> getAllBalances() {
        log.info("GET /api/balance - Fetching all balances");
        
        List<StockBalanceDTO> balances = balanceService.getAllBalances();
        
        return ResponseEntity.ok(
                ApiResponse.success(balances, "Lấy tồn kho thành công")
        );
    }
    
    /**
     * GET /api/balance/low-stock
     * Lấy danh sách hàng cạn
     */
    @GetMapping("/low-stock")
    @Operation(summary = "Lấy danh sách hàng cạn (cần nhập lại)")
    public ResponseEntity<ApiResponse<List<StockBalanceDTO>>> getLowStockItems() {
        log.info("GET /api/balance/low-stock - Fetching low stock items");
        
        List<StockBalanceDTO> lowStocks = balanceService.getLowStockItems();
        
        return ResponseEntity.ok(
                ApiResponse.success(lowStocks, "Lấy danh sách hàng cạn thành công")
        );
    }
    
    /**
     * GET /api/balance/out-of-stock
     * Lấy danh sách hàng hết
     */
    @GetMapping("/out-of-stock")
    @Operation(summary = "Lấy danh sách hàng hết")
    public ResponseEntity<ApiResponse<List<StockBalanceDTO>>> getOutOfStockItems() {
        log.info("GET /api/balance/out-of-stock - Fetching out of stock items");
        
        List<StockBalanceDTO> outOfStocks = balanceService.getOutOfStockItems();
        
        return ResponseEntity.ok(
                ApiResponse.success(outOfStocks, "Lấy danh sách hàng hết thành công")
        );
    }
    
    /**
     * PUT /api/balance/{materialId}/min-reorder-level
     * Set mức tối thiểu cần nhập lại
     */
    @PutMapping("/{materialId}/min-reorder-level")
    @Operation(summary = "Set mức tối thiểu cần nhập lại")
    public ResponseEntity<ApiResponse<StockBalanceDTO>> setMinReorderLevel(
            @PathVariable UUID materialId,
            @RequestParam Integer minLevel) {
        log.info("PUT /api/balance/{}/min-reorder-level - Setting to {}", materialId, minLevel);
        
        StockBalanceDTO balance = balanceService.setMinReorderLevel(materialId, minLevel);
        
        return ResponseEntity.ok(
                ApiResponse.success(balance, "Set mức tối thiểu thành công")
        );
    }
}