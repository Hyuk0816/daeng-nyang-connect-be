package com.git.backend.daeng_nyang_connect.review.board.service;


import com.git.backend.daeng_nyang_connect.animal.entity.AdoptedAnimal;
import com.git.backend.daeng_nyang_connect.review.board.dto.request.ReviewRequestDTO;
import com.git.backend.daeng_nyang_connect.review.board.dto.response.ReviewResponseDTO;
import com.git.backend.daeng_nyang_connect.review.board.entity.Review;
import com.git.backend.daeng_nyang_connect.review.board.entity.ReviewImage;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface ReviewService {
    // 입양 후기 작성
    Review addReview(Long animalId, ReviewRequestDTO reviewRequestDTO, List<MultipartFile> files, String token);

    // 입양 후기 삭제
    void deleteReview(Long reviewId, String token);

    // 입양 후기 수정
    Review updateReview(Long reviewId, ReviewRequestDTO reviewRequestDTO, String token);

    // 해당 파양동물의 입양 후기 출력
    List<Review> findAllReviewByAnimal(Long animalId);

    // 입양한 댕냥이에 대한 리뷰 전체 출력
    List<Review> findAllReview();

    // 해당 후기에 좋아요 클릭 - message 반환
    Map<String, String> likeReview(Long reviewId, String token);

    // 내가 작성한 후기인지 확인
    Review checkMyReview(Long reviewId, User user);

    // 내가 입양한 댕냥이인지 확인
    AdoptedAnimal checkMyAdoptedAnimal(Long animalId, User user);

    // 현재 시간을 한국 시간 Timestamp로 반환
    Timestamp nowDate();

    // Builder : 총 좋아요 수 반환
    void updateLike(Review review, Integer like);

    // 입양 후기 좋아요 알림
    void notifyPostLike(Review review);

    // Builder : 이미지 업로드
    void uploadImage(Review review, List<MultipartFile> multipartFileList);

    // 토큰으로 user 체크 후 user 반환
    User checkUserByToken(String token);

    // 원하는 response 값 저장 후 반환
    ReviewResponseDTO response(Review review);

    // 원하는 response 값 List에 저장 후 반환
    List<ReviewResponseDTO> responseList(List<Review> reviewList);

}
