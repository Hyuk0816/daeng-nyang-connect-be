package com.git.backend.daeng_nyang_connect.animal.repository;

import com.git.backend.daeng_nyang_connect.animal.entity.AdoptionStatus;
import com.git.backend.daeng_nyang_connect.animal.entity.Animal;
import com.git.backend.daeng_nyang_connect.animal.entity.Kind;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, Long> {

    List<Animal> findAnimalByKind(Kind kind);

    List<Animal> findAnimalByCity(String city);

    List<Animal> findAnimalByAdoptionStatus(AdoptionStatus adoptionStatus);
}
