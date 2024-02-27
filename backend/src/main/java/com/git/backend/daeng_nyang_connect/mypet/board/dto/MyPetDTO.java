package com.git.backend.daeng_nyang_connect.mypet.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.git.backend.daeng_nyang_connect.mypet.board.entity.MyPet;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyPetDTO {

    private Long boardId;
    private Long userId;
    private String nickname;
    private String userThumbnail;
    private String kind;
    private String text;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp createdAt;
    private Integer like;

    private String boardName;//게시판 이름

    public static MyPetDTO fromMyPetEntity(MyPet myPet, String nickname) {
        return MyPetDTO.builder()
                .boardId(myPet.getMyPetBoardId())
                .userId(myPet.getUser().getUserId())
                .nickname(nickname)
                .userThumbnail(myPet.getUser().getMyPage().getImg())
                .kind(myPet.getKind())
                .text(myPet.getText())
                .createdAt(myPet.getCreatedAt())
                .like(myPet.getMyPetLike())
                .build();
    }

}
