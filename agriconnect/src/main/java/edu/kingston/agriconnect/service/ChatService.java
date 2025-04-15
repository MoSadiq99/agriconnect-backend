package edu.kingston.agriconnect.service;

import edu.kingston.agriconnect.dto.ConversationDTO;
import edu.kingston.agriconnect.dto.MessageCreateDTO;
import edu.kingston.agriconnect.dto.MessageDTO;
import edu.kingston.agriconnect.model.Message;
import edu.kingston.agriconnect.model.User;
import edu.kingston.agriconnect.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private static final String CONVERSATION_CACHE_PREFIX = "conversation:";
    private final ChatRepository messageRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public MessageDTO sendMessage(MessageCreateDTO messageDto) {
        Long currentUserId = getCurrentUserId();
        if (!currentUserId.equals(messageDto.getSenderId())) {
            throw new SecurityException("Sender ID does not match authenticated user.");
        }

        Message message = new Message();
        message.setSenderId(messageDto.getSenderId());
        message.setRecipientId(messageDto.getRecipientId());
        message.setContent(messageDto.getContent());
        Message savedMessage = messageRepository.save(message);

        MessageDTO responseDto = mapToDto(savedMessage);

        // Todo Cache the latest message in Redis or a similar cache mechanism
//        String cacheKey = CONVERSATION_CACHE_PREFIX + Math.min(message.getSenderId(), message.getRecipientId()) +
//                ":" + Math.max(message.getSenderId(), message.getRecipientId());
//        redisTemplate.opsForValue().set(cacheKey, responseDto, 24, TimeUnit.HOURS);

        // WebSocket push
        messagingTemplate.convertAndSendToUser(
                message.getRecipientId().toString(), "/queue/private-messages", responseDto);
        messagingTemplate.convertAndSendToUser(
                message.getSenderId().toString(), "/queue/private-messages", responseDto);

        return responseDto;
    }

    @Transactional
    public Page<MessageDTO> getConversationHistory(Long userId1, Long userId2, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentTime").ascending());
        Page<Message> messages = messageRepository.findConversationBetweenUsers(userId1, userId2, pageable);

        // Mark messages as read for the current user
        Long currentUserId = getCurrentUserId();
        messages.getContent().stream()
                .filter(m -> m.getRecipientId().equals(currentUserId) && !m.isRead())
                .forEach(m -> {
                    m.setRead(true);
                    messageRepository.save(m);
                });

        return messages.map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public Page<ConversationDTO> getActiveConversations(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentTime").descending());
        Page<Message> latestMessages = messageRepository.findLatestMessagesForUser(userId, pageable);

        return latestMessages.map(message -> {
            Long otherUserId = message.getSenderId().equals(userId) ? message.getRecipientId() : message.getSenderId();
            User otherUser = userService.findById(otherUserId);

            ConversationDTO dto = new ConversationDTO();
            dto.setOtherUserId(otherUserId);
            dto.setOtherUserName(otherUser.getName());
            dto.setLastMessage(mapToDto(message));
            dto.setLastActivity(message.getSentTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            dto.setUnreadCount(getUnreadCount(userId, otherUserId)); // Optimized method
            return dto;
        });
    }

    private int getUnreadCount(Long userId, Long otherUserId) {
        return messageRepository.countByRecipientIdAndSenderIdAndReadFalse(userId, otherUserId);
    }

    private MessageDTO mapToDto(Message message) {
        // Same as before, but consider caching user details
        MessageDTO dto = new MessageDTO();
        dto.setMessageId(message.getMessageId());
        dto.setSenderId(message.getSenderId());
        dto.setRecipientId(message.getRecipientId());
        dto.setContent(message.getContent());
        dto.setSentTime(LocalDateTime.parse(message.getSentTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

        User sender = userService.findById(message.getSenderId());
        User recipient = userService.findById(message.getRecipientId());
        dto.setSenderName(sender != null ? sender.getName() : "User " + message.getSenderId());
        dto.setRecipientName(recipient != null ? recipient.getName() : "User " + message.getRecipientId());
        return dto;
    }

    public Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmailEager(username);
        return user != null ? user.getId() : null;
    }

    @Transactional
    public void deleteConversation(Long userId1, Long userId2) {
        // Get the authenticated user's ID
        Long currentUserId = getCurrentUserId();

        // Safety check: Ensure the current user is one of the participants
        if (!userId1.equals(currentUserId) && !userId2.equals(currentUserId)) {
            logger.warn("User {} attempted to delete a conversation they are not part of (userId1: {}, userId2: {})",
                    currentUserId, userId1, userId2);
            throw new AccessDeniedException("You can only delete your own conversations.");
        }

        // Validate input
        if (userId2 == null || userId1.equals(userId2)) {
            logger.error("Invalid user IDs for conversation deletion: userId1={}, userId2={}", userId1, userId2);
            throw new IllegalArgumentException("Invalid user IDs provided.");
        }

        // Perform the deletion
        int deletedCount = messageRepository.deleteConversationBetweenUsers(userId1, userId2);

        if (deletedCount > 0) {
            logger.info("Successfully deleted {} messages between user {} and user {}",
                    deletedCount, userId1, userId2);
        } else {
            logger.info("No messages found to delete between user {} and user {}", userId1, userId2);
        }
    }
}
