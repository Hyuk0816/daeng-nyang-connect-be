package com.git.backend.daeng_nyang_connect.mate.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.git.backend.daeng_nyang_connect.mate.comments.dto.MateCommentsResponseDTO;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MateResponseDTO {

    private Long boardId;
    private Long userId;
    private String nickname;
    private String userThumbnail;
    private String category;
    private String place;
    private String text;
    private List<String> img;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp createdAt;
    private List<MateCommentsResponseDTO> comments;
    private List<MateBoardLikeDTO> likes;

}
