package com.git.backend.daeng_nyang_connect.mate.comments.entity;

import com.git.backend.daeng_nyang_connect.mate.board.entity.Mate;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mate_comments")
public class MateComments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mate_comments_idx")
    private Long mateCommentsId;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @ManyToOne
    @JoinColumn(name = "mate_board_idx")
    private Mate mate;

    private String comment;
    @Column(name = "mate_comments_like")
    private Integer mateCommentsLike;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Getter
    @OneToMany(mappedBy = "mateComments", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MateCommentsLike> mateCommentsLikes;
    public void setMateCommentsLike(Integer mateCommentsLike) {
        this.mateCommentsLike = mateCommentsLike;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }


}
