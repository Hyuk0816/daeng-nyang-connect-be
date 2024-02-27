package com.git.backend.daeng_nyang_connect.mypet.board.entity;

import com.git.backend.daeng_nyang_connect.mypet.comments.entity.MyPetComments;
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
@Table(name = "my_pet")
public class MyPet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_pet_board_idx")
    private Long myPetBoardId;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    private String kind;
    private String breed;
    private String text;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column(name = "my_pet_like")
    private Integer myPetLike;

    @Getter
    @OneToMany(mappedBy = "myPet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MyPetImage> img;

    @Getter
    @OneToMany(mappedBy = "myPet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MyPetComments> comment;

    @Getter
    @OneToMany(mappedBy = "myPet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MyPetBoardLike> myPetLikes;
    public void setMyPetLike(Integer myPetLike) {
        this.myPetLike = myPetLike;
    }
    public void setKind(String kind){
        this.kind = kind;
    }
    public void setBreed(String breed){
        this.breed = breed;
    }
    public void setText(String text) {
        this.text = text;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
