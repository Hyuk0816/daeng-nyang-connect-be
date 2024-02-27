package com.git.backend.daeng_nyang_connect.review.board.entity;

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
@Table(name = "review_image")
public class ReviewImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_image_idx")
    private Long reviewImageId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "review_idx")
    private Review review;

    private String url;
}
