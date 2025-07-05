package com.pap_shop.exception;

import com.pap_shop.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * Global exception handler for the application.
 * This class uses {@link ControllerAdvice} to act as a central point for handling exceptions
 * thrown by any {@code @Controller} or {@code @RestController}. It ensures that all clients
 * receive a consistent and structured error response format.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Logger instance for logging error details, particularly for unexpected exceptions.
     */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles {@link ResourceNotFoundException}.
     * This exception is thrown when a requested resource (e.g., a product, user) cannot be found in the system.
     *
     * @param exception The {@link ResourceNotFoundException} instance that was thrown.
     * @param request The current web request.
     * @return A {@link ResponseEntity} containing a structured {@link ErrorResponse} and an HTTP status of 404 NOT_FOUND.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link IllegalArgumentException}.
     * This exception is typically thrown when a method receives an invalid or inappropriate argument,
     * often corresponding to invalid client input.
     *
     * @param exception The {@link IllegalArgumentException} instance that was thrown.
     * @param request The current web request.
     * @return A {@link ResponseEntity} containing a structured {@link ErrorResponse} and an HTTP status of 400 BAD_REQUEST.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * A "catch-all" handler for any other unhandled {@link Exception}.
     * This method acts as a safety net, ensuring that any unexpected server-side error is caught
     * and a generic, safe error message is returned to the client.
     * It logs the full exception details for debugging purposes.
     *
     * @param exception The {@link Exception} instance that was thrown.
     * @param request The current web request.
     * @return A {@link ResponseEntity} containing a generic error message in the {@link ErrorResponse}
     *         and an HTTP status of 500 INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception exception, WebRequest request) {
        // Log the full stack trace for server-side debugging
        logger.error("An unexpected error occurred: {}", exception.getMessage(), exception);

        // Create a generic error response to avoid leaking implementation details to the client
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected internal server error occurred.", // Generic message for the client
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}