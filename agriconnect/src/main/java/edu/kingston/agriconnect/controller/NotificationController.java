package edu.kingston.agriconnect.controller;

import edu.kingston.agriconnect.dto.NotificationDTO;
import edu.kingston.agriconnect.dto.NotificationRequest;
import edu.kingston.agriconnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    // GET /api/users/{userId}/notifications - Fetch all notifications for a user
    @GetMapping("/users/{userId}/notifications")
    @PreAuthorize("hasRole('ROLE_FARMER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_BUYER')")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications(@PathVariable Long userId) {
        List<NotificationDTO> notifications = notificationService.getAllNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // PUT /api/notifications/{notificationId}/read - Mark a notification as read
    @PutMapping("/notifications/{notificationId}/read")
    @PreAuthorize("hasRole('ROLE_FARMER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_BUYER')")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // POST /api/users/{userId}/notifications - Send a test notification (optional, for testing)
    @PostMapping("/users/{userId}/notifications")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<NotificationDTO> sendNotification(
            @PathVariable Long userId,
            @RequestBody NotificationRequest request) {
        NotificationDTO notificationDto = notificationService.sendNotification(userId, request.getMessage(), request.getType());
        return ResponseEntity.ok(notificationDto);
    }
}