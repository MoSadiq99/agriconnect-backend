package edu.kingston.agriconnect.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    private String message;
    private String type;
}