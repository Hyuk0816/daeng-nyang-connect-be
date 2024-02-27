package com.git.backend.daeng_nyang_connect.stomp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoom(ChatRoom chatRoom);

    @Query("SELECT m FROM Message m WHERE m.chatRoom=:chatRoom ORDER BY m.createdAt DESC Limit 1")
    Message findLatestMessage(@Param("chatRoom") ChatRoom chatRoom);
}
