package com.git.backend.daeng_nyang_connect.lost.board.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lost_image")
public class LostImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_idx")
    private Long lostImageId;

    private String url;

    @ManyToOne
    @JoinColumn(name = "lost_board_idx")
    @JsonBackReference(value = "Lost")
    private Lost lost;
}
