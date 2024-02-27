package com.git.backend.daeng_nyang_connect.notify.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.git.backend.daeng_nyang_connect.config.jwt.TokenProvider;
import com.git.backend.daeng_nyang_connect.lost.board.entity.Lost;
import com.git.backend.daeng_nyang_connect.mate.board.entity.Mate;
import com.git.backend.daeng_nyang_connect.mate.comments.entity.MateComments;
import com.git.backend.daeng_nyang_connect.mypet.board.entity.MyPet;
import com.git.backend.daeng_nyang_connect.mypet.comments.entity.MyPetComments;
import com.git.backend.daeng_nyang_connect.notify.dto.NotificationDTO;
import com.git.backend.daeng_nyang_connect.notify.entity.Notification;
import com.git.backend.daeng_nyang_connect.notify.repository.NotificationRepository;
import com.git.backend.daeng_nyang_connect.review.board.entity.Review;
import com.git.backend.daeng_nyang_connect.review.comments.entity.ReviewComments;
import com.git.backend.daeng_nyang_connect.stomp.ChatRoom;
import com.git.backend.daeng_nyang_connect.stomp.ChatRoomRepository;
import com.git.backend.daeng_nyang_connect.tips.board.entity.Tips;
import com.git.backend.daeng_nyang_connect.tips.comments.entity.TipsComments;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import com.git.backend.daeng_nyang_connect.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final Map<Long, List<Notification>> userNotifications = new ConcurrentHashMap<>();
    public static Map<Long, SseEmitter> sseEmitters = new HashMap<>();

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final NotificationRepository notificationRepository;

    private final Map<String, JpaRepository<?, Long>> boardTypeRepositories;
    private final Map<String, JpaRepository<?, Long>> commentTypeRepositories;

    @Autowired
    public NotificationService(
            UserRepository userRepository,
            TokenProvider tokenProvider,
            ChatRoomRepository chatRoomRepository,
            NotificationRepository notificationRepository,
            JpaRepository<Mate, Long> mateRepository,
            JpaRepository<MyPet, Long> myPetRepository,
            JpaRepository<Lost, Long> lostRepository,
            JpaRepository<Review, Long> reviewRepository,
            JpaRepository<Tips, Long> tipsBoardRepository,
            JpaRepository<MateComments, Long> mateCommentsRepository,
            JpaRepository<MyPetComments, Long> myPetCommentsRepository,
            JpaRepository<ReviewComments, Long> reviewCommentsRepository,
            JpaRepository<TipsComments, Long> tipsCommentsRepository
    ) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.chatRoomRepository = chatRoomRepository;
        this.notificationRepository = notificationRepository;

        this.boardTypeRepositories = Map.of(
                "댕냥메이트", mateRepository,
                "나의 댕냥이", myPetRepository,
                "댕냥 미아센터", lostRepository,
                "입양 후기", reviewRepository,
                "댕냥꿀팁", tipsBoardRepository
        );

        this.commentTypeRepositories = Map.of(
                "댕냥메이트", mateCommentsRepository,
                "나의 댕냥이", myPetCommentsRepository,
                "입양 후기", reviewCommentsRepository,
                "댕냥꿀팁", tipsCommentsRepository
        );
    }

    public void notifyComment(Long postId, String boardType) {
        String eventName = "addComment";
        String eventData = "내 게시물("+ boardType +")에 댓글이 달렸습니다.";

        Long userId = getUserIdFromEntityId(postId, boardTypeRepositories.get(boardType));
        sendNotification(userId, eventName, eventData);
    }

    public void notifyPostLike(Long postId, String boardType) {
        String eventName = "addLike";
        String eventData = "내 게시물("+ boardType +")에 좋아요가 추가되었습니다.";

        Long userId = getUserIdFromEntityId(postId, boardTypeRepositories.get(boardType));
        sendNotification(userId, eventName, eventData);
    }

    public void notifyCommentLike(Long commentId, String commentType) {
        String eventName = "addCommentLike";
        String eventData = "내 댓글("+ commentType +")에 좋아요가 추가되었습니다.";

        Long userId = getUserIdFromEntityId(commentId, commentTypeRepositories.get(commentType));
        sendNotification(userId, eventName, eventData);
    }

    private Long getUserIdFromEntityId(Long entityId, JpaRepository<?, Long> repository) {
        try {
            Object entity = repository.findById(entityId)
                    .orElseThrow(() -> new NoSuchElementException("게시글 또는 댓글을 찾을 수 없습니다. entityId: " + entityId));

            if (entity instanceof User) {
                return ((User) entity).getUserId();
            } else if (entity instanceof Mate) {
                return ((Mate) entity).getUser().getUserId();
            } else if (entity instanceof MyPet) {
                return ((MyPet) entity).getUser().getUserId();
            } else if (entity instanceof Lost) {
                return ((Lost) entity).getUser().getUserId();
            } else if (entity instanceof Review) {
                return ((Review) entity).getUser().getUserId();
            } else if (entity instanceof Tips) {
                return ((Tips) entity).getUser().getUserId();
            } else if (entity instanceof MateComments) {
                return ((MateComments) entity).getUser().getUserId();
            } else if (entity instanceof MyPetComments) {
                return ((MyPetComments) entity).getUser().getUserId();
            } else if (entity instanceof ReviewComments) {
                return ((ReviewComments) entity).getUser().getUserId();
            } else if (entity instanceof TipsComments) {
                return ((TipsComments) entity).getUser().getUserId();
            } else {
                throw new IllegalArgumentException("지원하지 않는 엔티티 형식입니다.");
            }
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("게시글 또는 댓글을 찾을 수 없습니다. entityId: " + entityId);
        }
    }

    public void notifyNewChatMessage(Long chatRoomId, String senderNickname) {
        String eventName = "newChatMessage";
        String eventData = senderNickname + "님이 메시지를 보냈습니다.";

        List<Long> userIds = getUserIdsFromChatRoom(chatRoomId);

        for (Long userId : userIds) {
            sendNotification(userId, eventName, eventData);
        }
    }

    private List<Long> getUserIdsFromChatRoom(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NoSuchElementException("채팅방을 찾을 수 없습니다. chatRoomId: " + chatRoomId));

        return chatRoom.getUserList()
                .stream()
                .map(user -> user.getUser().getUserId())
                .collect(Collectors.toList());
    }


    public void saveNotificationsToDatabase(Long userId, List<Notification> notifications) {
        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            for (Notification notification : notifications) {
                notification.setUser(user);
                notificationRepository.save(notification);
            }
        }
    }
    private static Notification buildNotification(String eventName, String eventData) {
        LocalDateTime currentTimestamp = LocalDateTime.now();
        return Notification.builder()
                .eventName(eventName)
                .eventData(eventData)
                .readTimestamp(currentTimestamp)
                .isRead(false)
                .build();
    }
    public void sendNotification(Long userId, String eventName, String eventData) {
        if (!userNotifications.containsKey(userId)) {
            userNotifications.put(userId, new ArrayList<>());
        }

        List<Notification> notifications = userNotifications.get(userId);
        notifications.add(buildNotification(eventName, eventData));

        if (sseEmitters.containsKey(userId)) {
            SseEmitter sseEmitter = sseEmitters.get(userId);
            try {
                sseEmitter.send(SseEmitter.event().name(eventName).data(eventData));
            } catch (Exception e) {
                sseEmitters.remove(userId);
                throw new RuntimeException("SseEmitter.send failed", e);
            }
        }
        saveNotificationsToDatabase(userId, notifications);
    }

    public List<NotificationDTO> getUnreadNotificationsDTO(String token) {
        User user = getUserFromToken(token);

        if (user != null) {
            List<Notification> unreadNotifications = getUnreadNotifications(user.getUserId());

            return unreadNotifications.stream()
                    .map(NotificationDTO::fromEntity)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserUserIdAndIsReadFalseOrderByReadTimestampDesc(userId);
    }

    public List<NotificationDTO> getReadNotificationsDTO(String token) {
        User user = getUserFromToken(token);
        if (user != null) {
            List<Notification> readNotifications = getReadNotifications(user.getUserId());
            markNotificationsAsRead(readNotifications);
            return readNotifications.stream()
                    .map(NotificationDTO::fromEntity)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<Notification> getReadNotifications(Long userId) {
        return notificationRepository.findByUserUserIdAndIsReadTrueOrderByReadTimestampDesc(userId);
    }

    public void markNotificationsAsRead(List<Notification> notifications) {
        for (Notification notification : notifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    public void handleNotificationConfirmation(Long userId, Long notificationId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);

        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();

            if (notification.getUser().getUserId().equals(userId)) {
                notification.setRead(true);
                notificationRepository.save(notification);
            } else {
                throw new AccessDeniedException("사용자의 알림이 아닙니다.");
            }
        } else {
            throw new NotFoundException("알림을 찾을 수 없습니다.");
        }
    }

    public void deleteNotification(Long userId, Long notificationId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();

            if (notification.getUser().getUserId().equals(userId)) {
                notificationRepository.delete(notification);
            } else {
                throw new AccessDeniedException("사용자의 알림이 아닙니다.");
            }
        } else {
            throw new NotFoundException("알림을 찾을 수 없습니다.");
        }
    }

    @Scheduled(cron = "0 0 1 * * ?") // 매일 새벽 1시에 실행
    public void deleteOldNotifications() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(10); // 10일 전 알림 삭제
        notificationRepository.deleteByIsReadTrueAndReadTimestampBefore(cutoffTime);
    }

    public SseEmitter createSseEmitter(String providedToken) {
        if (providedToken != null) {
            User currentUser = getUserByToken(providedToken);
            if (currentUser != null) {
                Long userId = currentUser.getUserId();

                if (sseEmitters.containsKey(userId)) {
                    return sseEmitters.get(userId);
                }

                SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1시간
                sseEmitters.put(userId, emitter);
                return emitter;
            }
        }

        return null;
    }

    public void removeEmitter(String providedToken) {
        if (providedToken != null) {
            User currentUser = getUserByToken(providedToken);
            if (currentUser != null) {
                Long userId = currentUser.getUserId();
                sseEmitters.remove(userId);
            }
        }
    }

    public User getUserFromToken(String token) {
        String userEmail = tokenProvider.getEmailBytoken(token);
        return userRepository.findByEmail(userEmail).orElse(null);
    }

    private User getUserByToken(String token) {
        return userRepository.findByEmail(tokenProvider.getEmailBytoken(token))
                .orElse(null);
    }

}