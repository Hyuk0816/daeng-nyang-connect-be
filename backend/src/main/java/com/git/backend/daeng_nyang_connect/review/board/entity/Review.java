package com.git.backend.daeng_nyang_connect.review.board.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.git.backend.daeng_nyang_connect.animal.entity.AdoptedAnimal;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "review")
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_idx")
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @OneToOne
    @JoinColumn(name = "adopted_animal_idx")
    private AdoptedAnimal adoptedAnimal;

    @Column(name = "text_review")
    private String textReview;

    @Column(name = "review_like")
    private Integer reviewLike;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ReviewImage> images;
}
