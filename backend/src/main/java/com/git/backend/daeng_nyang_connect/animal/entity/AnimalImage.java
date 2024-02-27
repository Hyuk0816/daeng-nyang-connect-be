package com.git.backend.daeng_nyang_connect.animal.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "animal_image")
public class AnimalImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "animal_image_idx")
    private Long animalImageId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "animal_board_idx")
    private Animal animal;

    private String url;
}
