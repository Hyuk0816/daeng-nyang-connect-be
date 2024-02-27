package com.git.backend.daeng_nyang_connect.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "my_page")
public class MyPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_page_idx")
    private Long myPageId;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_idx", unique = true)
    private User user;

    private String info;
    private String img;
}
