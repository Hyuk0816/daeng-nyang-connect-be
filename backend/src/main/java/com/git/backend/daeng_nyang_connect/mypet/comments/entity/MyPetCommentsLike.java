package com.git.backend.daeng_nyang_connect.mypet.comments.entity;

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
@Table(name = "my_pet_comments_like")
public class MyPetCommentsLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_pet_comments_like_idx")
    private Long myPetCommentsLikeId;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @ManyToOne
    @JoinColumn(name = "my_pet_comments_idx")
    private MyPetComments myPetComments;

    public MyPetCommentsLike(MyPetComments myPetComments, User user) {
        this.myPetComments = myPetComments;
        this.user = user;
    }
}
