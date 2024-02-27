package com.git.backend.daeng_nyang_connect.stomp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chat_room_user")
public class ChatRoomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_user_idx")
    private Long chatRoomUserId;


    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "chat_room_idx")
    private ChatRoom chatRoom;

//    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    // Getter, Setter, 생성자 등 필요한 메서드 구현
}

