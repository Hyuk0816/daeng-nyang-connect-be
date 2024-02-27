package com.git.backend.daeng_nyang_connect.animal.repository;

import com.git.backend.daeng_nyang_connect.animal.entity.Animal;
import com.git.backend.daeng_nyang_connect.animal.entity.AnimalScrap;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnimalScrapRepository extends JpaRepository<AnimalScrap, Long> {

    List<AnimalScrap> findByUser(User user);

    void deleteByUser(User user);


    @Query("SELECT a FROM AnimalScrap a WHERE a.user =:user AND a.animal=:animal")
    Optional<AnimalScrap> findMyScrapList(@Param("user") User user, @Param("animal") Animal animal);
}
