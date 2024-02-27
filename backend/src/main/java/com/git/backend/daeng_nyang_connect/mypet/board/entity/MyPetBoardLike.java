package com.git.backend.daeng_nyang_connect.mypet.board.entity;

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
@Table(name = "my_pet_board_like")
public class MyPetBoardLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_pet_board_like_idx")
    private Long myPetBoardLikeId;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @ManyToOne
    @JoinColumn(name = "my_pet_board_idx")
    private MyPet myPet;

    public MyPetBoardLike(MyPet myPet, User user) {
        this.myPet = myPet;
        this.user = user;
    }
}
