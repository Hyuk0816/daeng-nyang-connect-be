package com.git.backend.daeng_nyang_connect.review.comments.dto.request;

import com.git.backend.daeng_nyang_connect.review.board.entity.Review;
import com.git.backend.daeng_nyang_connect.review.comments.entity.ReviewComments;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCommentsRequestDTO {
    private String comment;

    public void checkUpdateList(ReviewCommentsRequestDTO commentDTO, ReviewComments reviewComments) {
        if (Objects.isNull(commentDTO.getComment())) {
            this.comment = reviewComments.getComment();
        }
    }
}
