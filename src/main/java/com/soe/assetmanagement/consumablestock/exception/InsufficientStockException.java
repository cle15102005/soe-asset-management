package com.soe.assetmanagement.consumablestock.exception;

/**
 * Exception: Insufficient stock (400)
 */
public class InsufficientStockException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private Integer requiredQuantity;
    private Integer availableQuantity;
    
    public InsufficientStockException(String message) {
        super(message);
    }
    
    public InsufficientStockException(String message, Integer required, Integer available) {
        super(message);
        this.requiredQuantity = required;
        this.availableQuantity = available;
    }
    
    public Integer getRequiredQuantity() {
        return requiredQuantity;
    }
    
    public Integer getAvailableQuantity() {
        return availableQuantity;
    }
}