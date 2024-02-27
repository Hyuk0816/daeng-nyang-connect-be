package com.git.backend.daeng_nyang_connect.mate.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MateCommentsResponseDTO {
    private Long commentsId;
    private Long userId;
    private String nickname;
    private String userThumbnail;
    private String comment;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp createdAt;
    private List<MateCommentsLikeDTO> likes;
}

