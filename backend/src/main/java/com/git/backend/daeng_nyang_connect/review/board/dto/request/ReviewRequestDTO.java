package com.git.backend.daeng_nyang_connect.review.board.dto.request;

import com.git.backend.daeng_nyang_connect.animal.entity.AdoptedAnimal;
import com.git.backend.daeng_nyang_connect.review.board.entity.Review;
import com.git.backend.daeng_nyang_connect.review.board.entity.ReviewImage;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDTO {


    private String textReview;
//    List<MultipartFile> files;
    public void checkUpdateList(ReviewRequestDTO reviewRequestDTO, Review animal) {
        if (Objects.isNull(reviewRequestDTO.getTextReview())) {
            this.textReview = animal.getTextReview();
        }
    }
}
