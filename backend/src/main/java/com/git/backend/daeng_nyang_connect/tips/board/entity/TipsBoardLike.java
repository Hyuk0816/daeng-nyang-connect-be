package com.git.backend.daeng_nyang_connect.tips.board.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "tips_board_like")
public class TipsBoardLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tips_board_like_idx")
    private Long tipsBoardLikeId;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "tips_board_idx")
    @JsonBackReference(value = "tipsLike")
    private Tips tips;


    public TipsBoardLike(Tips tips, User user) {
        this.tips = tips;
        this.user = user;
    }
}
