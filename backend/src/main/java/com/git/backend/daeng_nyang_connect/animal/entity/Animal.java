package com.git.backend.daeng_nyang_connect.animal.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "animal")
public class Animal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "animal_board_idx")
    private Long animalId;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @Column(name = "animal_name")
    private String animalName;

    private String age;
    private String gender;
    private String disease;
    private String breed; // 종

    @Column(name = "text_reason")
    private String textReason; // 파양 이유

    @Column(name = "text_etc")
    private String textEtc;

    @Column(name = "nurture_period")
    private Integer nurturePeriod; // 입양 시기

    private String training;
    private Boolean neutering; // 중성화 여부

    @Enumerated(EnumType.STRING)
    private Kind kind; // 강아지 or 고양이 or 기타

    @Column(name = "adoption_status")
    @Enumerated(EnumType.STRING)
    private AdoptionStatus adoptionStatus; // 입양 완료 여부

    @Column(name = "health_check")
    private String healthCheck;

    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp createdAt;

    private String city;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<AnimalImage> images = new ArrayList<>();

    public void updateAdoptionStatus(AdoptionStatus adoptionStatus){
        this.adoptionStatus = adoptionStatus;
    }
}
