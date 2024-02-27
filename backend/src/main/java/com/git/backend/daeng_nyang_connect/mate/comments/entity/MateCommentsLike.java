package com.git.backend.daeng_nyang_connect.mate.comments.entity;

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
@Table(name = "mate_comments_like")
public class MateCommentsLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mate_comments_like_idx")
    private Long mateCommentsLikeId;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @ManyToOne
    @JoinColumn(name = "mate_comments_idx")
    private MateComments mateComments;

    public MateCommentsLike( MateComments mateComments, User user) {
        this.mateComments = mateComments;
        this.user = user;
    }
}
