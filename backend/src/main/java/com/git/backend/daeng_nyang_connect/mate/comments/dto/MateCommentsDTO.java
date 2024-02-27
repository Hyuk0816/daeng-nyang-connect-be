package com.git.backend.daeng_nyang_connect.mate.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.git.backend.daeng_nyang_connect.mate.board.entity.Mate;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MateCommentsDTO {
    private Long commentsId;
    private Long userId;
    private String nickname;
    private String userThumbnail;
    private Mate mate;
    private String comment;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp createdAt;
    private Integer like;
    private List<MateCommentsLikeDTO> likes;
}