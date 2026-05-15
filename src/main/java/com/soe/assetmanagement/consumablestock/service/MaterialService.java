package com.soe.assetmanagement.consumablestock.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soe.assetmanagement.consumablestock.dto.CreateMaterialRequest;
import com.soe.assetmanagement.consumablestock.dto.MaterialDTO;
import com.soe.assetmanagement.consumablestock.dto.UpdateMaterialRequest;
import com.soe.assetmanagement.consumablestock.entity.Material;
import com.soe.assetmanagement.consumablestock.entity.StockBalance;
import com.soe.assetmanagement.consumablestock.exception.InvalidTransactionException;
import com.soe.assetmanagement.consumablestock.exception.ResourceNotFoundException;
import com.soe.assetmanagement.consumablestock.repository.MaterialRepository;
import com.soe.assetmanagement.consumablestock.repository.StockBalanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.extern.slf4j.Slf4j;

/**
 * Service: MaterialService
 * Business logic cho CS-01: Material catalogue management
 */
@Service
@Slf4j
@Transactional
public class MaterialService {
    
    @Autowired
    private MaterialRepository materialRepository;
    
    @Autowired
    private StockBalanceRepository stockBalanceRepository;
    
    /**
     * Tạo vật tư mới + auto-create StockBalance
     */
    public MaterialDTO create(CreateMaterialRequest request, UUID userId) {
        log.info("Creating new material with code: {}", request.getMaterialCode());
        
        // Kiểm tra mã vật tư không trùng
        if (materialRepository.existsByMaterialCode(request.getMaterialCode())) {
            throw new InvalidTransactionException(
                    "Mã vật tư '" + request.getMaterialCode() + "' đã tồn tại");
        }
        
        // Tạo Material entity
        Material material = Material.builder()
                .materialCode(request.getMaterialCode())
                .materialName(request.getMaterialName())
                .category(request.getCategory())
                .description(request.getDescription())
                .technicalSpecs(request.getTechnicalSpecs())
                .unitOfMeasure(request.getUnitOfMeasure())
                .supplier(request.getSupplier())
                .isActive(true)
                .createdBy(userId)
                .build();
        
        Material savedMaterial = materialRepository.save(material);
        log.info("Material created successfully with ID: {}", savedMaterial.getId());
        
        // AUTO-CREATE StockBalance
        StockBalance balance = StockBalance.builder()
                .material(savedMaterial)
                .currentQuantity(0)
                .unit(savedMaterial.getUnitOfMeasure())
                .minReorderLevel(0)
                .build();
        
        stockBalanceRepository.save(balance);
        log.info("StockBalance created automatically for material: {}", savedMaterial.getId());
        
        return convertToDTO(savedMaterial);
    }
    
    /**
     * Lấy tất cả vật tư
     */
    @Transactional(readOnly = true)
    public List<MaterialDTO> getAll() {
        return materialRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    /**
     * Lấy tất cả vật tư (có phân trang)
     */
    @Transactional(readOnly = true)
    public Page<MaterialDTO> getAllPaginated(Pageable pageable) {
        return materialRepository.findByIsActiveTrue(pageable)
                .map(this::convertToDTO);
    }
    
    /**
     * Lấy vật tư theo ID
     */
    @Transactional(readOnly = true)
    public MaterialDTO getById(UUID id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vật tư ID " + id + " không tồn tại"));
        return convertToDTO(material);
    }
    
    /**
     * Lấy vật tư theo mã
     */
    @Transactional(readOnly = true)
    public MaterialDTO getByCode(String code) {
        Material material = materialRepository.findByMaterialCode(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vật tư mã '" + code + "' không tồn tại"));
        return convertToDTO(material);
    }
    
    /**
     * Tìm kiếm vật tư theo tên
     */
    @Transactional(readOnly = true)
    public List<MaterialDTO> searchByName(String keyword) {
        return materialRepository.searchByName(keyword)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy vật tư theo phân loại
     */
    @Transactional(readOnly = true)
    public List<MaterialDTO> getByCategory(String category) {
        return materialRepository.findByCategory(category)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Cập nhật vật tư
     */
    public MaterialDTO update(UUID id, UpdateMaterialRequest request) {
        log.info("Updating material with ID: {}", id);
        
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vật tư ID " + id + " không tồn tại"));
        
        if (request.getMaterialName() != null) {
            material.setMaterialName(request.getMaterialName());
        }
        if (request.getCategory() != null) {
            material.setCategory(request.getCategory());
        }
        if (request.getDescription() != null) {
            material.setDescription(request.getDescription());
        }
        if (request.getTechnicalSpecs() != null) {
            material.setTechnicalSpecs(request.getTechnicalSpecs());
        }
        if (request.getSupplier() != null) {
            material.setSupplier(request.getSupplier());
        }
        if (request.getIsActive() != null) {
            material.setIsActive(request.getIsActive());
        }
        
        Material updated = materialRepository.save(material);
        log.info("Material updated successfully");
        
        return convertToDTO(updated);
    }
    
    /**
     * Xóa vật tư (soft delete)
     */
    public void delete(UUID id) {
        log.info("Deleting material with ID: {}", id);
        
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vật tư ID " + id + " không tồn tại"));
        
        material.setIsActive(false);
        materialRepository.save(material);
        log.info("Material deleted successfully (soft delete)");
    }
    
    /**
     * Convert Material entity to DTO
     */
    private MaterialDTO convertToDTO(Material material) {
        return MaterialDTO.builder()
                .id(material.getId())
                .materialCode(material.getMaterialCode())
                .materialName(material.getMaterialName())
                .category(material.getCategory())
                .description(material.getDescription())
                .technicalSpecs(material.getTechnicalSpecs())
                .unitOfMeasure(material.getUnitOfMeasure())
                .supplier(material.getSupplier())
                .createdAt(material.getCreatedAt())
                .updatedAt(material.getUpdatedAt())
                .isActive(material.getIsActive())
                .build();
    }
}