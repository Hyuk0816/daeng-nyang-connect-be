package com.git.backend.daeng_nyang_connect.tips.comments.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tips_comments_like")
public class TipsCommentsLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tips_comments_like_idx")
    private Long tipsCommentsLikeId;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    @JsonBackReference(value = "userLikeReference")
    private User user;

    @ManyToOne
    @JoinColumn(name = "tips_comments_idx")
    @JsonBackReference(value = "tipsCommentsLikeReference")
    private TipsComments tipsComments;

    public TipsCommentsLike(User user, TipsComments tipsComments) {
        this.user = user;
        this.tipsComments = tipsComments;
    }
}
