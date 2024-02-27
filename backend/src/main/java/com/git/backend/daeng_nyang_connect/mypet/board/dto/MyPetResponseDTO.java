package com.git.backend.daeng_nyang_connect.mypet.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.git.backend.daeng_nyang_connect.mypet.comments.dto.MyPetCommentsResponseDTO;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyPetResponseDTO {
    private Long boardId;
    private Long userId;
    private String nickname;
    private String userThumbnail;
    private String kind;
    private String text;
    private List<String> img;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp createdAt;
    private List<MyPetCommentsResponseDTO> comments;
    private List<MyPetBoardLikeDTO> likes;
}
