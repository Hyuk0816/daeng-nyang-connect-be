package com.git.backend.daeng_nyang_connect.review.comments.service;


import com.git.backend.daeng_nyang_connect.review.comments.dto.request.ReviewCommentsRequestDTO;
import com.git.backend.daeng_nyang_connect.review.comments.dto.response.ReviewCommentsResponseDTO;
import com.git.backend.daeng_nyang_connect.review.comments.entity.ReviewComments;
import com.git.backend.daeng_nyang_connect.user.entity.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface ReviewCommentsService {
    // 댓글 작성
    ReviewComments addCommentsOnReview(Long reviewId, ReviewCommentsRequestDTO commentDTO, String token);

    // 댓글 삭제
    void deleteCommentsOnReview(Long reviewCommentsId, String token);

    // 댓글 수정
    ReviewComments updateCommentsOnReview(Long reviewCommentsId, ReviewCommentsRequestDTO commentDTO, String token);

    // 해당 후기에 대한 댓글 출력
    List<ReviewComments> findAllCommentsByReview(Long reviewId);

    // 해당 댓글에 좋아요 클릭 - message 반환
    Map<String, String> likeCommentsOnReview(Long reviewCommentsId, String token);

    // 내가 작성한 댓글인지 확인
    ReviewComments checkMyComments(Long reviewCommentsId, User user);

    // 현재 시간을 한국 시간 Timestamp로 반환
    Timestamp nowDate();

    // Builder : 총 좋아요 수 반환
    void updateLike(ReviewComments reviewComments, Integer like);

    // 댓글 좋아요 알림
    void notifyCommentLike(ReviewComments reviewComments);

    // 토큰으로 user 체크 후 user 반환
    User checkUserByToken(String token);

    // 원하는 response 값 저장 후 반환
    ReviewCommentsResponseDTO response(ReviewComments reviewComments);

    // 원하는 response 값 List에 저장 후 반환
    List<ReviewCommentsResponseDTO> responseList(List<ReviewComments> reviewCommentList);

}
