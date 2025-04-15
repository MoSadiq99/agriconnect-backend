package edu.kingston.agriconnect.service;

import edu.kingston.agriconnect.dto.NotificationDTO;
import edu.kingston.agriconnect.model.Notification;
import edu.kingston.agriconnect.model.User;
import edu.kingston.agriconnect.repository.NotificationRepository;
import edu.kingston.agriconnect.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public NotificationDTO sendNotification(Long userId, String message, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setSentTime(LocalDateTime.now());
        notification.setRead(false);

        Notification savedNotification = notificationRepository.save(notification);
        NotificationDTO notificationDto = new NotificationDTO(savedNotification);

        // Send to WebSocket topic matching frontend subscription
        messagingTemplate.convertAndSend("/topic/users/" + userId + "/notifications", notificationDto);

        return notificationDto;
    }

    public List<NotificationDTO> getAllNotifications(Long userId) {
        return notificationRepository.findByUserId(userId)
                .stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}