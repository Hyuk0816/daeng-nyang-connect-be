package com.git.backend.daeng_nyang_connect.lost.comments.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LostCommentsDTO {
    private Long commentsId;
    private Long boardId;
    private Long userId;
    private String comment;
    private Timestamp createdAt;
    private String nickname;
    private String userThumbnail;
}
