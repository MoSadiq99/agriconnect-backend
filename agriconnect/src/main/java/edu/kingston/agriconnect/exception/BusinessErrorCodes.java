package edu.kingston.agriconnect.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BusinessErrorCodes {
    NO_CODE(0, "No code", HttpStatus.NOT_IMPLEMENTED),
    INCORRECT_CURRENT_PASSWORD(300, "Incorrect current password", HttpStatus.BAD_REQUEST),
    NEW_PASSWORDS_DO_NOT_MATCH(301, "New passwords do not match", HttpStatus.BAD_REQUEST),
    ACCOUNT_LOCKED(302, "User account is locked", HttpStatus.FORBIDDEN),
    ACCOUNT_DISABLED(303, "User account is disabled", HttpStatus.FORBIDDEN),
    BAD_CREDENTIALS(304, "Invalid credentials", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(305, "Invalid token", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED(306, "Token expired", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND(307, "User not found", HttpStatus.NOT_FOUND), // Changed to NOT_FOUND
    USER_NOT_VERIFIED(308, "User not verified", HttpStatus.FORBIDDEN),
    USER_ALREADY_EXISTS(309, "User already exists", HttpStatus.CONFLICT),
    USER_ALREADY_VERIFIED(310, "User already verified", HttpStatus.CONFLICT),
    USER_ALREADY_REGISTERED(311, "User already registered", HttpStatus.CONFLICT),
    USER_NOT_REGISTERED(313, "User not registered", HttpStatus.NOT_FOUND), // Changed to NOT_FOUND
    USER_NOT_LOGGED_IN(314, "User not logged in", HttpStatus.UNAUTHORIZED),

    FARMER_NOT_FOUND(400, "Farmer not found", HttpStatus.NOT_FOUND),
    CULTIVATION_NOT_FOUND(401, "Cultivation not found", HttpStatus.NOT_FOUND),
    FARMER_LISTING_NOT_FOUND(402, "Farmer listing not found", HttpStatus.NOT_FOUND),

    BUYER_NOT_FOUND(403, "Buyer not found", HttpStatus.NOT_FOUND),
    BUYER_LISTING_NOT_FOUND(404, "Buyer listing not found", HttpStatus.NOT_FOUND),

    CONVERSATION_NOT_FOUND(405, "Conversation not found", HttpStatus.NOT_FOUND),
    CONVERSATION_LISTING_NOT_FOUND(406, "Conversation listing not found", HttpStatus.NOT_FOUND),
    MESSAGE_NOT_FOUND(407, "Message not found", HttpStatus.NOT_FOUND),
    MESSAGE_LISTING_NOT_FOUND(408, "Message listing not found", HttpStatus.NOT_FOUND),;

    BusinessErrorCodes(int code, String description, HttpStatus httpStatus) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }

    private final int code;
    private final String description;
    private final HttpStatus httpStatus;
}