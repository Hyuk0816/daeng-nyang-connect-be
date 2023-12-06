package com.git.backend.daeng_nyang_connect.review.comments.service;


import com.git.backend.daeng_nyang_connect.review.board.entity.Review;
import com.git.backend.daeng_nyang_connect.review.comments.entity.ReviewComments;
import com.git.backend.daeng_nyang_connect.review.comments.entity.ReviewCommentsLike;
import com.git.backend.daeng_nyang_connect.user.entity.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface ReviewCommentsService {
    // 댓글 작성
    ReviewComments addCommentsOnReview(Long reviewId, String commentsRequestDTO, String token);

    // 댓글 삭제
    void deleteCommentsOnReview(Long reviewCommentsId, String token);

    // 댓글 수정
    ReviewComments updateCommentsOnReview(Long reviewCommentsId, String updateCommentsRequestDTO, String token);

    // 해당 후기에 대한 댓글 출력
    List<ReviewComments> findAllCommentsByReview(Long reviewId);

    // 해당 댓글에 좋아요 클릭 - message 반환
    Map<String, String> likeCommentsOnReview(Long reviewCommentsId, String token);

    // 내가 작성한 댓글인지 확인
    ReviewComments checkMyComments(Long reviewCommentsId, User user);

    // 현재 시간을 한국 시간 Timestamp로 반환
    Timestamp nowDate();

    // Builder : 총 좋아요 수 반환
    ReviewComments updateLike(ReviewComments reviewComments, Integer like);
}
