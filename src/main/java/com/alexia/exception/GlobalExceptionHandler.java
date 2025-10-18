package com.alexia.exception;

import com.alexia.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Manejador global de excepciones para la aplicación.
 * Captura y procesa excepciones de manera centralizada.
 * 
 * @author Alexia Team
 * @version 1.0
 * @since 2025-10-15
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Maneja excepciones de conexión a base de datos.
     */
    @ExceptionHandler(DatabaseConnectionException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseConnectionException(
            DatabaseConnectionException ex, HttpServletRequest request) {
        
        log.error("Error de conexión a base de datos - path={}, exception={}, message={}", 
                request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.create(
                ex.getMessage(),
                "Database Connection Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
    
    /**
     * Maneja excepciones de Telegram.
     */
    @ExceptionHandler(TelegramException.class)
    public ResponseEntity<ErrorResponse> handleTelegramException(
            TelegramException ex, HttpServletRequest request) {
        
        log.error("Error en operación de Telegram - path={}, exception={}, message={}", 
                request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.create(
                ex.getMessage(),
                "Telegram Error",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
    
    /**
     * Maneja excepciones genéricas no capturadas.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        log.error("Error inesperado - path={}, exception={}, message={}", 
                request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.create(
                "Ha ocurrido un error inesperado",
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
