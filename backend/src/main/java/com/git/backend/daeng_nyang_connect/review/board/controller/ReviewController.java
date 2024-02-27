package com.git.backend.daeng_nyang_connect.review.board.controller;

import com.git.backend.daeng_nyang_connect.review.board.dto.request.ReviewRequestDTO;
import com.git.backend.daeng_nyang_connect.review.board.dto.response.ReviewResponseDTO;
import com.git.backend.daeng_nyang_connect.review.board.entity.Review;
import com.git.backend.daeng_nyang_connect.review.board.service.ReviewService;
import com.git.backend.daeng_nyang_connect.review.board.service.ReviewServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "후기 게시물 API")
@RequestMapping("/api/review")
@EnableCaching
public class ReviewController {
    //    CRUD : 입양 해온 동물에 대한 리뷰 등록 * 삭제 * 정보 수정 * 조회
    private final ReviewService reviewService;
    private final ReviewServiceImpl reviewServiceImpl;

    @Operation(summary = "게시물 작성")
    @PostMapping("/post")
    public ResponseEntity<?> addReview(@RequestParam("animalId") Long animalId,
                                       ReviewRequestDTO reviewRequestDTO,
                                       List<MultipartFile> files,
                                       @RequestHeader("access_token") String token){
        Review newReview = reviewService.addReview(animalId, reviewRequestDTO, files, token);
        ReviewResponseDTO response = reviewService.response(newReview);
        return ResponseEntity.status(200).body(response);
    }

    @Transactional
    @Operation(summary = "게시물 삭제")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteReview(@RequestParam("reviewId") Long reviewId,
                                            @RequestHeader("access_token") String token){
        reviewService.deleteReview(reviewId, token);
        return ResponseEntity.status(200).body("게시글이 삭제되었습니다.");
    }

    @Transactional
    @Operation(summary = "게시물 수정")
    @PutMapping("/modify")
    public ResponseEntity<?> updateReview(@RequestParam("reviewId") Long reviewId,
                                          @RequestBody ReviewRequestDTO reviewRequestDTO,
                                          @RequestHeader("access_token") String token){
        Review updateReview = reviewService.updateReview(reviewId, reviewRequestDTO, token);
        ReviewResponseDTO response = reviewService.response(updateReview);
        return ResponseEntity.status(200).body(response);
    }

    // 입양간 동물에 대한 리뷰 전체 출력
    @Operation(summary = "게시물 전체 조회")
    @GetMapping("/getAll")
    public ResponseEntity<?> findAllReview(){
        List<Review> reviewList = reviewService.findAllReview();
        List<ReviewResponseDTO> responseList = reviewService.responseList(reviewList);
        return ResponseEntity.status(200).body(responseList);
    }

    // 원하는 입양간 동물에 대한 리뷰 전체 출력
    @Operation(summary = "원하는 입양간 동물에 대한 리뷰 전체 조회")
    @GetMapping()
    public ResponseEntity<?> findAllReviewByAnimalId(@RequestParam("animalId") Long animalId){
        List<Review> reviewList = reviewService.findAllReviewByAnimal(animalId);
        List<ReviewResponseDTO> responseList = reviewService.responseList(reviewList);
        return ResponseEntity.status(200).body(responseList);
    }
    @Transactional
    @Operation(summary = "게시물 좋아요")
    @PostMapping("/like")
    public ResponseEntity<?> likeReview(@RequestParam("reviewId") Long reviewId,
                                          @RequestHeader("access_token") String token){
        Map<String, String> message = reviewService.likeReview(reviewId, token);
        return ResponseEntity.status(200).body(message);
    }

    @Operation(summary = "게시물 사이즈")
    @GetMapping("/getSize")
    public Map<String,Integer>getSize(){
        return reviewServiceImpl.getSize();
    }
}
