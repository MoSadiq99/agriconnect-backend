package edu.kingston.agriconnect.dto;

import lombok.Data;

@Data
public class ConversationDTO {
    private Long otherUserId;
    private String otherUserName;
    private MessageDTO lastMessage;
    private int unreadCount;
    private String lastActivity;
}
