package edu.kingston.agriconnect.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BusinessException extends RuntimeException {
    private final int code;
    private final String description;
    private final HttpStatus httpStatus;
    private final Map<String, Object> metadata = new HashMap<>(); // Optional metadata

    public BusinessException(BusinessErrorCodes errorCode) {
        super(errorCode.getDescription());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
        this.httpStatus = errorCode.getHttpStatus();
    }

    public BusinessException(BusinessErrorCodes errorCode, String customMessage) {
        super(customMessage);
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
        this.httpStatus = errorCode.getHttpStatus();
    }

    public BusinessException withMetadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }
}