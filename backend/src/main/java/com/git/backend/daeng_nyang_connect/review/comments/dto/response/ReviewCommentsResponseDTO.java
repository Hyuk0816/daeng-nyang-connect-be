package com.git.backend.daeng_nyang_connect.review.comments.dto.response;

import com.git.backend.daeng_nyang_connect.review.comments.entity.ReviewComments;
import lombok.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCommentsResponseDTO {
    private Long commentsId;
    private Long boardId;
    private String nickname;
    private String adoptedAnimalName;
    private String textReview;
    private String comment;
    private String createdAt;
    private String userThumbnail;
    private Integer reviewCommentLike;

    public ReviewCommentsResponseDTO(ReviewComments reviewComments) {
        this.commentsId = reviewComments.getReviewCommentsId();
        this.boardId = reviewComments.getReview().getReviewId();
        this.nickname = reviewComments.getUser().getNickname();
        this.adoptedAnimalName = reviewComments.getReview().getAdoptedAnimal().getAnimal().getAnimalName();
        this.textReview = reviewComments.getReview().getTextReview();
        this.comment = reviewComments.getComment();
        this.createdAt = TimestampToFormattedString(reviewComments.getCreatedAt());
        this.userThumbnail = reviewComments.getUser().getMyPage().getImg();
        this.reviewCommentLike = reviewComments.getReviewCommentLike();
    }

    public String TimestampToFormattedString(Timestamp time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
        return dateFormat.format(time);
    }
}
