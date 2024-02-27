package com.git.backend.daeng_nyang_connect.notify.dto;

import com.git.backend.daeng_nyang_connect.notify.entity.Notification;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDTO {
    private Long notificationId;
    private String eventName;
    private String eventData;
    private LocalDateTime readTimestamp;
    private boolean isRead;

    public static NotificationDTO fromEntity(Notification notification) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setNotificationId(notification.getNotificationId());
        notificationDTO.setEventName(notification.getEventName());
        notificationDTO.setEventData(notification.getEventData());
        notificationDTO.setReadTimestamp(notification.getReadTimestamp());
        notificationDTO.setRead(notification.isRead());
        return notificationDTO;
    }
}
