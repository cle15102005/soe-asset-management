package vn.edu.hust.soict.soe.assetmanagement.exception;

/**
 * Thrown when a requested resource does not exist.
 * Converted to HTTP 404 by GlobalExceptionHandler.
 *
 * Usage:
 *   throw new ResourceNotFoundException("Asset not found with id: " + id);
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}