package com.git.backend.daeng_nyang_connect.lost.comments.entity;

import com.git.backend.daeng_nyang_connect.lost.board.entity.Lost;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lost_comments")
public class LostComments {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lost_comments_idx")
    private Long lostCommentsId;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @ManyToOne
    @JoinColumn(name = "lost_board_idx")
    private Lost lost;

    private String comment;

    @Column(name = "created_at")
    private Timestamp createdAt;
}
