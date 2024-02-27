package com.git.backend.daeng_nyang_connect.user.dto;

import com.git.backend.daeng_nyang_connect.animal.entity.*;
import lombok.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyScrapAnimalResponseDTO {
    private Long animalId;
    private String animalName;
    private String age;
    private String gender;
    private String disease;
    private String training;
    private Boolean neutering;
    private String textReason;
    private String healthCheck;
    private Kind kind;
    private String breed;
    private Integer nurturePeriod;
    private String city;
    private AdoptionStatus adoptionStatus; // 입양 완료 여부
    private Timestamp createdAt;
    private String userThumbnail;
    private List<String> images = new ArrayList<>();

    public MyScrapAnimalResponseDTO(AnimalScrap animal, List<AnimalImage> animalImages) {
        Animal thisAnimal = animal.getAnimal();
        this.animalId = thisAnimal.getAnimalId();
        this.animalName = thisAnimal.getAnimalName();
        this.age = thisAnimal.getAge();
        this.gender = thisAnimal.getGender();
        this.disease = thisAnimal.getDisease();
        this.training = thisAnimal.getTraining();
        this.neutering = thisAnimal.getNeutering();
        this.textReason = thisAnimal.getTextReason();
        this.healthCheck = thisAnimal.getHealthCheck();
        this.kind = thisAnimal.getKind();
        this.breed = thisAnimal.getBreed();
        this.nurturePeriod = thisAnimal.getNurturePeriod();
        this.city = thisAnimal.getCity();
        this.adoptionStatus = thisAnimal.getAdoptionStatus();
        this.createdAt = thisAnimal.getCreatedAt();
        this.userThumbnail = animal.getUser().getMyPage().getImg();

        for (AnimalImage animalImage : animalImages) {
            this.images.add(animalImage.getUrl());
        }
    }

    public String TimestampToFormattedString(Timestamp time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
        return dateFormat.format(time);
    }
}
