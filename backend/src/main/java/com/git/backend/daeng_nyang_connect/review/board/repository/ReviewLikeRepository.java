package com.git.backend.daeng_nyang_connect.review.board.repository;

import com.git.backend.daeng_nyang_connect.review.board.entity.Review;
import com.git.backend.daeng_nyang_connect.review.board.entity.ReviewLike;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

//    좋아요 수 합산
    @Query("SELECT count(al) AS totalLike FROM ReviewLike al WHERE al.review.reviewId=:reviewId")
    Integer totalReviewLike(@Param("reviewId")Long reviewId);

    Optional<ReviewLike> findByUser(User user);

    void deleteByUser(User user);

    List<ReviewLike> findAllByUser(User user);
}
