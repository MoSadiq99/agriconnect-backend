package edu.kingston.agriconnect.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private ResponseEntity<ExceptionResponse> buildResponse(HttpStatus status, BusinessErrorCodes errorCode, String message) {
        return ResponseEntity.status(status)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(errorCode.getCode())
                        .businessErrorDescription(errorCode.getDescription())
                        .error(message)
                        .build());
    }

    private ResponseEntity<ExceptionResponse> buildResponse(BusinessException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(ExceptionResponse.builder()
                        .businessErrorCode(ex.getCode())
                        .businessErrorDescription(ex.getDescription())
                        .error(ex.getMessage())
                        .metadata(ex.getMetadata())
                        .build());
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleLockedException(LockedException e) {
        log.warn("Account locked: {}", e.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, BusinessErrorCodes.ACCOUNT_LOCKED, e.getMessage());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleDisabledException(DisabledException e) {
        log.warn("Account disabled: {}", e.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, BusinessErrorCodes.ACCOUNT_DISABLED, e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentials(BadCredentialsException e) {
        log.warn("Bad credentials: {}", e.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, BusinessErrorCodes.BAD_CREDENTIALS, e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException e) {
        log.warn("Business exception: {} - {}", e.getCode(), e.getMessage());
        return buildResponse(e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException e) {
        Set<String> validationErrors = e.getBindingResult().getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toSet());
        log.warn("Validation failed: {}", validationErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .validationErrors(validationErrors)
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleConstraintViolation(ConstraintViolationException e) {
        Set<String> validationErrors = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        log.warn("Constraint violation: {}", validationErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .validationErrors(validationErrors)
                        .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidRequestBody(HttpMessageNotReadableException e) {
        log.warn("Invalid request body: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .error("Invalid request body")
                        .build());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEntityNotFound(EntityNotFoundException e) {
        log.warn("Entity not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.CULTIVATION_NOT_FOUND.getCode()) // Adjust based on context
                        .businessErrorDescription("Resource not found")
                        .error(e.getMessage())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception e) {
        log.error("Unexpected error occurred: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.NO_CODE.getCode())
                        .businessErrorDescription("Internal Server Error, Please Contact Admin")
                        .error(e.getMessage())
                        .build());
    }
}