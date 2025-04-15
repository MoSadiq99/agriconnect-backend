package edu.kingston.agriconnect.dto;

import lombok.Data;

@Data
public class MessageCreateDTO {
    private Long senderId;
    private Long recipientId;
    private String content;
    private boolean read;
}
