package com.git.backend.daeng_nyang_connect.lost.board.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lost")
public class Lost {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lost_board_idx")
    private Long lostBoardId;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @Column(name = "category")
    private String category;

    @Column(name = "place")
    private String place;

    @Column(name = "reward")
    private String reward;

    @Column(name = "kind")
    private String kind;

    @Column(name = "breed")
    private String breed;

    @Column(name = "gender")
    private String gender;

    @Column(name = "color")
    private String color;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "lost_date")
    private Date lostDate;

    @Column(name = "lost_time")
    private String lostTime;

    @Column(name = "text")
    private String text;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @OneToMany(mappedBy = "lost", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "Lost")
    private List<LostImage> images;
}
