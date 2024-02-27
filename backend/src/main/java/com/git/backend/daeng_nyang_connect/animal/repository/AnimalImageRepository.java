package com.git.backend.daeng_nyang_connect.animal.repository;

import com.git.backend.daeng_nyang_connect.animal.entity.Animal;
import com.git.backend.daeng_nyang_connect.animal.entity.AnimalImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnimalImageRepository extends JpaRepository<AnimalImage, Long> {
    List<AnimalImage> findByAnimal(Animal animal);

    void deleteByAnimal(Animal animal);
}
