package com.git.backend.daeng_nyang_connect.tips.board.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.git.backend.daeng_nyang_connect.tips.comments.entity.TipsComments;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tips")
public class Tips {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tips_board_idx")
    private Long tipsBoardId;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    private String category;
    private String title;
    private String text;

    @Column(name = "tips_like")
    private Integer tipsLike;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @OneToMany(mappedBy = "tips", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "tipsLike")
    List<TipsBoardLike> likeList;

    @OneToMany(mappedBy = "tips",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "tipsImgReference")
    List<TipsImage> images;

    @OneToMany(mappedBy = "tips", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "tipsCommentsReference")
    List<TipsComments> comments;


    public void setTipsLike(Integer tipsLike) {
        this.tipsLike = tipsLike;
    }
}
