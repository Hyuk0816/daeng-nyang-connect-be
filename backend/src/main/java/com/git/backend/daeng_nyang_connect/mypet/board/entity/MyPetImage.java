package com.git.backend.daeng_nyang_connect.mypet.board.entity;

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
@Table(name = "my_pet_image")
public class MyPetImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_idx")
    private Long myPetImgId;

    @ManyToOne
    @JoinColumn(name = "my_pet_board_idx")
    private MyPet myPet;

    private String url;
}
