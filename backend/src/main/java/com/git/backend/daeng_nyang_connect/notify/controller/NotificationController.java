package com.git.backend.daeng_nyang_connect.notify.controller;

import com.git.backend.daeng_nyang_connect.notify.dto.NotificationDTO;
import com.git.backend.daeng_nyang_connect.notify.service.NotificationService;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.function.Consumer;

@Cacheable
@RestController
@RequiredArgsConstructor
@Tag(name = "실시간 알림")
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "안 읽은 알림 가져오기")
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(@RequestHeader("access_token") String token) {
        List<NotificationDTO> unreadNotifications = notificationService.getUnreadNotificationsDTO(token);
        return ResponseEntity.ok(unreadNotifications);
    }

    @Operation(summary = "읽은 알림 가져오기")
    @GetMapping("/read")
    public ResponseEntity<List<NotificationDTO>> getReadNotifications(
            @RequestHeader("access_token") String token) {
        List<NotificationDTO> readNotifications = notificationService.getReadNotificationsDTO(token);
        return ResponseEntity.ok(readNotifications);
    }

    @Operation(summary = "알림 확인 처리")
    @PostMapping("/check")
    public ResponseEntity<Void> confirmNotification(
            @RequestHeader("access_token") String token,
            @RequestParam("id") Long notificationId) {
        return handleNotificationOperation(token, userId -> notificationService.handleNotificationConfirmation(userId, notificationId));
    }

    @Operation(summary = "알림 삭제")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteNotification(
            @RequestHeader("access_token") String token,
            @RequestParam("id") Long notificationId) {
        return handleNotificationOperation(token, userId -> notificationService.deleteNotification(userId, notificationId));
    }

    private ResponseEntity<Void> handleNotificationOperation(String token, Consumer<Long> notificationOperation) {
        User user = notificationService.getUserFromToken(token);
        if (user != null) {
            notificationOperation.accept(user.getUserId());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @Operation(summary = "실시간 알림 / SSE 에미터 생성")
    @GetMapping("/sse-emitter")
    public ResponseEntity<SseEmitter> createSseEmitter(@RequestHeader("access_token") String token) {
        SseEmitter sseEmitter = notificationService.createSseEmitter(token);
        return ResponseEntity.ok(sseEmitter);
    }

    @Operation(summary = "SSE 에미터 제거")
    @DeleteMapping("/sse-emitter")
    public ResponseEntity<Void> removeSseEmitter(@RequestHeader("access_token") String token) {
        notificationService.removeEmitter(token);
        return ResponseEntity.ok().build();
    }
}
