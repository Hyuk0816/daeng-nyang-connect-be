package com.git.backend.daeng_nyang_connect.mypet.comments.entity;

import com.git.backend.daeng_nyang_connect.mypet.board.entity.MyPet;
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
@Table(name = "my_pet_comments")
public class MyPetComments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_pet_comments_idx")
    private Long myPetCommentsId;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @ManyToOne
    @JoinColumn(name = "my_pet_board_idx")
    private MyPet myPet;

    private String comment;
    @Column(name = "my_pet_comments_like")
    private Integer myPetCommentsLike;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Getter
    @OneToMany(mappedBy = "myPetComments", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MyPetCommentsLike> myPetCommentsLikes;
    public void setMyPetCommentsLike(Integer myPetCommentsLike) {
        this.myPetCommentsLike = myPetCommentsLike;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

}
