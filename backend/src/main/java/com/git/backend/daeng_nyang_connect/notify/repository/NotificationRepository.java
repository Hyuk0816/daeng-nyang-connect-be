package com.git.backend.daeng_nyang_connect.notify.repository;

import com.git.backend.daeng_nyang_connect.notify.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserUserIdAndIsReadFalseOrderByReadTimestampDesc(Long userId);

    List<Notification> findByUserUserIdAndIsReadTrueOrderByReadTimestampDesc(Long userId);

    void deleteByIsReadTrueAndReadTimestampBefore(LocalDateTime cutoffTime);
}
