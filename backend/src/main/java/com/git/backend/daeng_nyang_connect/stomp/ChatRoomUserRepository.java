package com.git.backend.daeng_nyang_connect.stomp;

import com.git.backend.daeng_nyang_connect.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    ChatRoomUser findByUser(User sender);

    @Query("SELECT cru.chatRoom FROM ChatRoomUser cru WHERE cru.user.userId=:userId")
    List<ChatRoom> findMyChatRoom(@Param("userId") Long userId);
}
