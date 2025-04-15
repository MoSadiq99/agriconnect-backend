package edu.kingston.agriconnect.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "chat_messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private Long recipientId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime sentTime;

    @Column(nullable = false)
    private boolean read = false; // New field to track read status

    @PrePersist
    protected void onCreate() {
        sentTime = LocalDateTime.now();
    }
}