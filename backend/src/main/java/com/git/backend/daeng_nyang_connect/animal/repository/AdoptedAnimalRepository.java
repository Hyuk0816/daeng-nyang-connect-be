package com.git.backend.daeng_nyang_connect.animal.repository;

import com.git.backend.daeng_nyang_connect.animal.entity.AdoptedAnimal;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdoptedAnimalRepository extends JpaRepository<AdoptedAnimal, Long> {

    @Query("SELECT a FROM AdoptedAnimal a WHERE a.animal.animalId=:animalId")
    Optional<AdoptedAnimal> findByAnimalId(@Param("animalId") Long animalId);

    List<AdoptedAnimal> findByUser(User user);
}
