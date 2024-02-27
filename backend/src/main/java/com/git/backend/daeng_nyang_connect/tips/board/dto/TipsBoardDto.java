package com.git.backend.daeng_nyang_connect.tips.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.git.backend.daeng_nyang_connect.tips.board.entity.Tips;
import lombok.*;


import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TipsBoardDto {

    private Long boardId;
    private String category;
    private String title;
    private String text;
    private Integer like;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp createdAt;

    //게시글 조회시 유저 닉네임 필요
    private String  nickname;

    private String boardName; //게시판 이름



    public static TipsBoardDto fromEntity(Tips tips,String nickname){
        return TipsBoardDto.builder()
                .boardId(tips.getTipsBoardId())
                .category(tips.getCategory())
                .title(tips.getTitle())
                .text(tips.getText())
                .nickname(nickname)
                .createdAt(tips.getCreatedAt())
                .like(tips.getTipsLike())
                .build();
    }

}
