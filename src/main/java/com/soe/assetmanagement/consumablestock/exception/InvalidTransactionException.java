package com.soe.assetmanagement.consumablestock.exception;

/**
 * Exception: Invalid transaction (400)
 */
public class InvalidTransactionException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public InvalidTransactionException(String message) {
        super(message);
    }
    
    public InvalidTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}