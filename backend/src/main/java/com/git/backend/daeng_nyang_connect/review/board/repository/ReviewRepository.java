package com.git.backend.daeng_nyang_connect.review.board.repository;

import com.git.backend.daeng_nyang_connect.review.board.entity.Review;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.adoptedAnimal.animal.animalId=:animalId")
    List<Review> findReviewByAnimalId(@Param("animalId") Long animalId);

    Optional<Review> findByUser(User user);

    List<Review> findByUserNickname(String nickname);

}
