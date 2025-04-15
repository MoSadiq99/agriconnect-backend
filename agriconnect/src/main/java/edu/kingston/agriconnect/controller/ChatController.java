package edu.kingston.agriconnect.controller;

import edu.kingston.agriconnect.dto.ConversationDTO;
import edu.kingston.agriconnect.dto.MessageCreateDTO;
import edu.kingston.agriconnect.dto.MessageDTO;
import edu.kingston.agriconnect.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageCreateDTO messageDto) {
        return ResponseEntity.ok(chatService.sendMessage(messageDto));
    }

    @GetMapping("/conversation")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<MessageDTO>> getConversationHistory(
            @RequestParam("userId1") Long userId1,
            @RequestParam("userId2") Long userId2,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(chatService.getConversationHistory(userId1, userId2, page, size));
    }

    @DeleteMapping("/conversation")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteConversation(
            @RequestParam("userId1") Long userId1,
            @RequestParam("userId2") Long userId2) {
        chatService.deleteConversation(userId1, userId2);
        return ResponseEntity.ok("Conversation deleted successfully");
    }

    @GetMapping("/conversations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ConversationDTO>> getActiveConversations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long currentUserId = chatService.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(chatService.getActiveConversations(currentUserId, page, size));
    }
}