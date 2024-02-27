package com.git.backend.daeng_nyang_connect.review.comments.repository;

import com.git.backend.daeng_nyang_connect.review.board.entity.Review;
import com.git.backend.daeng_nyang_connect.review.comments.entity.ReviewComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewCommentsRepository extends JpaRepository<ReviewComments, Long> {

    @Query("SELECT rc FROM ReviewComments rc WHERE rc.review.reviewId=:reviewId")
    List<ReviewComments> findAllCommentsByReviewId(@Param("reviewId")Long reviewId);
}
