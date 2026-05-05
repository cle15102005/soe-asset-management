package vn.edu.hust.soict.soe.assetmanagement.exception;

/**
 * Thrown when an operation violates a business rule.
 * Converted to HTTP 400 by GlobalExceptionHandler.
 *
 * Usage:
 *   throw new BusinessRuleException("Asset is already liquidated.");
 *   throw new BusinessRuleException("Người khởi tạo không thể tự phê duyệt yêu cầu.");
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}