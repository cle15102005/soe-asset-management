package com.soe.assetmanagement.consumablestock.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soe.assetmanagement.consumablestock.dto.IssueRequest;
import com.soe.assetmanagement.consumablestock.dto.ReceiptRequest;
import com.soe.assetmanagement.consumablestock.dto.StockTransactionDTO;
import com.soe.assetmanagement.consumablestock.entity.Material;
import com.soe.assetmanagement.consumablestock.entity.StockBalance;
import com.soe.assetmanagement.consumablestock.entity.StockTransaction;
import com.soe.assetmanagement.consumablestock.exception.InsufficientStockException;
import com.soe.assetmanagement.consumablestock.exception.ResourceNotFoundException;
import com.soe.assetmanagement.consumablestock.repository.MaterialRepository;
import com.soe.assetmanagement.consumablestock.repository.StockBalanceRepository;
import com.soe.assetmanagement.consumablestock.repository.StockTransactionRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Service: StockTransactionService
 * Business logic cho CS-02: Stock receipt & issue tracking
 */
@Service
@Slf4j
@Transactional
public class StockTransactionService {
    
    @Autowired
    private StockTransactionRepository transactionRepository;
    
    @Autowired
    private MaterialRepository materialRepository;
    
    @Autowired
    private StockBalanceRepository balanceRepository;
    
    /**
     * Tạo giao dịch nhập kho + UPDATE StockBalance
     */
    public StockTransactionDTO createReceipt(ReceiptRequest request, UUID userId) {
        log.info("Creating receipt for material ID: {}, quantity: {}", 
                request.getMaterialId(), request.getQuantity());
        
        // Lấy Material
        Material material = materialRepository.findById(request.getMaterialId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vật tư ID " + request.getMaterialId() + " không tồn tại"));
        
        // Tạo StockTransaction
        StockTransaction transaction = StockTransaction.builder()
                .material(material)
                .transactionType(StockTransaction.TransactionType.RECEIPT)
                .quantity(request.getQuantity())
                .unit(material.getUnitOfMeasure())
                .transactionDate(request.getTransactionDate())
                .notes(request.getNotes())
                .createdBy(userId)
                .build();
        
        StockTransaction savedTransaction = transactionRepository.save(transaction);
        log.info("Receipt created successfully with ID: {}", savedTransaction.getId());
        
        // UPDATE StockBalance
        StockBalance balance = balanceRepository.findByMaterialId(request.getMaterialId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "StockBalance không tồn tại cho vật tư ID: " + request.getMaterialId()));
        
        balance.setCurrentQuantity(balance.getCurrentQuantity() + request.getQuantity());
        balance.setLastReceiptDate(request.getTransactionDate());
        balanceRepository.save(balance);
        
        log.info("StockBalance updated: quantity now = {}", balance.getCurrentQuantity());
        
        return convertToDTO(savedTransaction);
    }
    
    /**
     * Tạo giao dịch xuất kho + UPDATE StockBalance
     * Kiểm tra tồn kho trước khi xuất
     */
    public StockTransactionDTO createIssue(IssueRequest request, UUID userId) {
        log.info("Creating issue for material ID: {}, quantity: {}", 
                request.getMaterialId(), request.getQuantity());
        
        // Lấy Material
        Material material = materialRepository.findById(request.getMaterialId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vật tư ID " + request.getMaterialId() + " không tồn tại"));
        
        // Kiểm tra tồn kho đủ không
        StockBalance balance = balanceRepository.findByMaterialId(request.getMaterialId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "StockBalance không tồn tại cho vật tư ID: " + request.getMaterialId()));
        
        if (balance.getCurrentQuantity() < request.getQuantity()) {
            log.warn("Insufficient stock for material: {}. Required: {}, Available: {}",
                    material.getMaterialCode(), request.getQuantity(), balance.getCurrentQuantity());
            throw new InsufficientStockException(
                    "Hàng tồn không đủ cho vật tư '" + material.getMaterialCode() + "'",
                    request.getQuantity(),
                    balance.getCurrentQuantity());
        }
        
        // Tạo StockTransaction
        StockTransaction transaction = StockTransaction.builder()
                .material(material)
                .transactionType(StockTransaction.TransactionType.ISSUE)
                .quantity(request.getQuantity())
                .unit(material.getUnitOfMeasure())
                .transactionDate(request.getTransactionDate())
                .notes(request.getNotes())
                .createdBy(userId)
                .build();
        
        StockTransaction savedTransaction = transactionRepository.save(transaction);
        log.info("Issue created successfully with ID: {}", savedTransaction.getId());
        
        // UPDATE StockBalance
        balance.setCurrentQuantity(balance.getCurrentQuantity() - request.getQuantity());
        balance.setLastIssueDate(request.getTransactionDate());
        balanceRepository.save(balance);
        
        log.info("StockBalance updated: quantity now = {}", balance.getCurrentQuantity());
        
        return convertToDTO(savedTransaction);
    }
    
    /**
     * Lấy tất cả transactions của 1 vật tư
     */
    @Transactional(readOnly = true)
    public List<StockTransactionDTO> getByMaterialId(UUID materialId) {
        return transactionRepository.findByMaterialIdOrderByTransactionDateDesc(materialId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    /**
     * Lấy transaction theo ID
     */
    @Transactional(readOnly = true)
    public StockTransactionDTO getById(UUID id) {
        StockTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Giao dịch ID " + id + " không tồn tại"));
        return convertToDTO(transaction);
    }
    /**
     * Lấy transactions theo khoảng thời gian
     */
    @Transactional(readOnly = true)
    public List<StockTransactionDTO> getByDateRange(UUID materialId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByDateRange(materialId, startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert StockTransaction entity to DTO
     */
    private StockTransactionDTO convertToDTO(StockTransaction transaction) {
        return StockTransactionDTO.builder()
                .id(transaction.getId())
                .materialId(transaction.getMaterial().getId())
                .materialCode(transaction.getMaterial().getMaterialCode())
                .materialName(transaction.getMaterial().getMaterialName())
                .transactionType(transaction.getTransactionType())
                .quantity(transaction.getQuantity())
                .unit(transaction.getUnit())
                .transactionDate(transaction.getTransactionDate())
                .referenceDocNum(transaction.getReferenceDocNum())
                .notes(transaction.getNotes())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}