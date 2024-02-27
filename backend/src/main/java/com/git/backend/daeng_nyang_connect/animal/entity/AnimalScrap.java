package com.git.backend.daeng_nyang_connect.animal.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "animal_scrap")
public class AnimalScrap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "animal_scrap_idx")
    private Long animalScrapId;

    @ManyToOne
    @JsonBackReference(value = "scrap")
    @JoinColumn(name = "user_idx")
    private User user;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "animal_board_idx")
    private Animal animal;
}
