package com.git.backend.daeng_nyang_connect.mypet.comments.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyPetCommentsResponseDTO {
    private Long commentsId;
    private Long userId;
    private String nickname;
    private String userThumbnail;
    private String comment;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp createdAt;
    private List<MyPetCommentsLikeDTO> likes;
}
