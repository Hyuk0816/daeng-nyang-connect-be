package com.git.backend.daeng_nyang_connect.animal.entity;

import com.git.backend.daeng_nyang_connect.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "adopted_animal")
public class AdoptedAnimal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adopted_animal_idx")
    private Long adoptedAnimalId;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @OneToOne
    @JoinColumn(name = "animal_board_idx")
    private Animal animal;

    @Temporal(TemporalType.DATE)
    @Column(name = "adopted_date")
    private Date adoptedDate;
}
