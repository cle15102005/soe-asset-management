package vn.edu.hust.soict.soe.assetmanagement.exception;

/**
 * Thrown when an authenticated user attempts an operation
 * outside their role's permitted scope.
 * Converted to HTTP 403 by GlobalExceptionHandler.
 *
 * Note: This is a custom exception separate from Spring Security's
 * AccessDeniedException. Use this for application-level permission
 * checks (e.g. unit-scoped access), not for Spring Security RBAC.
 *
 * Usage:
 *   throw new AccessDeniedException("Bạn không có quyền truy cập đơn vị này.");
 */
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}