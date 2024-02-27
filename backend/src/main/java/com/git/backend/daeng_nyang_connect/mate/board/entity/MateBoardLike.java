package com.git.backend.daeng_nyang_connect.mate.board.entity;

import com.git.backend.daeng_nyang_connect.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mate_board_like")
public class MateBoardLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mate_board_like_idx")
    private Long mateBoardLikeId;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @ManyToOne
    @JoinColumn(name = "mate_board_idx")
    private Mate mate;

    public MateBoardLike(Mate mate, User user) {
        this.mate = mate;
        this.user = user;
    }

}
