package com.git.backend.daeng_nyang_connect.mypet.board.repository;

import com.git.backend.daeng_nyang_connect.mypet.board.entity.MyPet;
import com.git.backend.daeng_nyang_connect.mypet.board.entity.MyPetImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MyPetImageRepository extends JpaRepository<MyPetImage, Long> {

    Optional<List<MyPetImage>> findByMyPet(MyPet myPet);

}
