package com.git.backend.daeng_nyang_connect.tips.comments.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.git.backend.daeng_nyang_connect.tips.comments.entity.TipsCommentsLike;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TipsCommentsDto {

    private Long commentsId;

    @JsonBackReference("tipsCommentsReference")
    private Long boardId;

    @JsonBackReference("userReference")
    private Long userId;

    private String comment;
    private Timestamp createdAt;
    private Integer like;
    private String nickname;
    private String userThumbnail;


    private List<TipsCommentsLike> likes;

}
