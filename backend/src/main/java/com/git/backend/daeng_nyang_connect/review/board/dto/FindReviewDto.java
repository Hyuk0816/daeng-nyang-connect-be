package com.git.backend.daeng_nyang_connect.review.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindReviewDto {

    private Long boardId;
    private String text;

    private String boardName; //게시판 이름

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp createdAt;
}
