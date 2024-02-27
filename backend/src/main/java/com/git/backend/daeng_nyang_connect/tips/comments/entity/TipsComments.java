package com.git.backend.daeng_nyang_connect.tips.comments.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.git.backend.daeng_nyang_connect.tips.board.entity.Tips;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tips_comments")
public class TipsComments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tips_comments_idx")
    private Long tipsCommentsId;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    @JsonBackReference(value = "userReference")
    private User user;

    @ManyToOne
    @JoinColumn(name = "tips_board_idx")
    @JsonBackReference(value = "tipsCommentsReference")
    private Tips tips;

    private String comment;

    private Integer tipsCommentsLike;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @OneToMany(mappedBy = "tipsComments", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "tipsCommentsLikeReference")
    private List<TipsCommentsLike> likeList;
}
