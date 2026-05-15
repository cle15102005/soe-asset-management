package com.soe.assetmanagement.consumablestock.service;

import com.soe.assetmanagement.consumablestock.dto.StockBalanceDTO;
import com.soe.assetmanagement.consumablestock.entity.StockBalance;
import com.soe.assetmanagement.consumablestock.exception.ResourceNotFoundException;
import com.soe.assetmanagement.consumablestock.repository.StockBalanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service: StockBalanceService
 * Business logic cho CS-03: Real-time stock balance
 */
@Service
@Slf4j
@Transactional
public class StockBalanceService {
    
    @Autowired
    private StockBalanceRepository balanceRepository;
    
    /**
     * Lấy số lượng hiện tại tồn kho
     */
    @Transactional(readOnly = true)
    public Integer getCurrentBalance(UUID materialId) {
        return balanceRepository.findByMaterialId(materialId)
                .map(StockBalance::getCurrentQuantity)
                .orElse(0);
    }
    
    /**
     * Lấy chi tiết tồn kho
     */
    @Transactional(readOnly = true)
    public StockBalanceDTO getBalance(UUID materialId) {
        StockBalance balance = balanceRepository.findByMaterialId(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Tồn kho cho vật tư ID " + materialId + " không tồn tại"));
        return convertToDTO(balance);
    }
    
    /**
     * Lấy danh sách hàng cạn (cần nhập lại)
     */
    @Transactional(readOnly = true)
    public List<StockBalanceDTO> getLowStockItems() {
        return balanceRepository.findLowStockItems()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy danh sách hàng hết
     */
    @Transactional(readOnly = true)
    public List<StockBalanceDTO> getOutOfStockItems() {
        return balanceRepository.findOutOfStockItems()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy danh sách tất cả tồn kho
     */
    @Transactional(readOnly = true)
    public List<StockBalanceDTO> getAllBalances() {
        return balanceRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Set mức tối thiểu cần nhập lại
     */
    public StockBalanceDTO setMinReorderLevel(UUID materialId, Integer minLevel) {
        log.info("Setting min reorder level for material ID: {} to {}", materialId, minLevel);
        
        StockBalance balance = balanceRepository.findByMaterialId(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Tồn kho không tồn tại"));
        
        balance.setMinReorderLevel(minLevel);
        StockBalance updated = balanceRepository.save(balance);
        
        return convertToDTO(updated);
    }
    
    /**
     * Convert to DTO
     */
    private StockBalanceDTO convertToDTO(StockBalance balance) {
        return StockBalanceDTO.builder()
                .id(balance.getId())
                .materialId(balance.getMaterial().getId())
                .materialCode(balance.getMaterial().getMaterialCode())
                .materialName(balance.getMaterial().getMaterialName())
                .currentQuantity(balance.getCurrentQuantity())
                .unit(balance.getUnit())
                .lastReceiptDate(balance.getLastReceiptDate())
                .lastIssueDate(balance.getLastIssueDate())
                .minReorderLevel(balance.getMinReorderLevel())
                .needsReorder(balance.getCurrentQuantity() <= balance.getMinReorderLevel())
                .updatedAt(balance.getUpdatedAt())
                .build();
    }
}