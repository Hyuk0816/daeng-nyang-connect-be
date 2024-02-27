package com.git.backend.daeng_nyang_connect.mate.board.entity;

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
@Table(name = "mate_image")
public class MateImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mate_img_idx")
    private Long mateImgId;

    @ManyToOne
    @JoinColumn(name = "mate_board_idx")
    private Mate mate;

    private String url;
}
