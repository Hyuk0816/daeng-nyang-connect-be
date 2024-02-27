package com.git.backend.daeng_nyang_connect.notify.entity;

import com.git.backend.daeng_nyang_connect.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_idx")
    private Long notificationId;

    private String eventName;
    private String eventData;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @Column(name = "read_timestamp", columnDefinition = "TIMESTAMP")
    private LocalDateTime readTimestamp;

    private boolean isRead;
}
