package com.git.backend.daeng_nyang_connect.mate.board.entity;

import com.git.backend.daeng_nyang_connect.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mate")
public class Mate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mate_board_idx")
    private Long mateBoardId;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    private String category;
    private String place;
    private String text;

    @Column(name = "created_at")
    private Timestamp createdAt;

    private Integer like;
}
