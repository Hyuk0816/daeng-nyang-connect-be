package com.git.backend.daeng_nyang_connect.lost.board.dto;

import com.git.backend.daeng_nyang_connect.lost.board.entity.Lost;
import com.git.backend.daeng_nyang_connect.lost.board.entity.LostImage;
import com.git.backend.daeng_nyang_connect.lost.comments.dto.LostCommentsDTO;
import com.git.backend.daeng_nyang_connect.lost.comments.entity.LostComments;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LostBoardDetailDTO {
    private Long boardId;
    private String category;
    private String place;
    private String reward;
    private String mobile;
    private String kind;
    private String breed;
    private String gender;
    private String color;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date lostDate;
    private String lostTime;
    private String text;
    private Timestamp createdAt;
    private List<LostImage> images;

    private Long userId;
    private String nickname;
    private String userThumbnail;

    private List<LostCommentsDTO> comments;

    public static LostBoardDetailDTO fromEntity
            (Lost lost, List<LostImage> lostImages,
             List<LostCommentsDTO> lostComments){
        return LostBoardDetailDTO.builder()
                .boardId(lost.getLostBoardId())
                .userId(lost.getUser().getUserId())
                .category(lost.getCategory())
                .place(lost.getPlace())
                .reward(lost.getReward())
                .mobile(lost.getMobile())
                .kind(lost.getKind())
                .breed(lost.getBreed())
                .gender(lost.getGender())
                .color(lost.getColor())
                .lostDate(lost.getLostDate())
                .lostTime(lost.getLostTime())
                .createdAt(lost.getCreatedAt())
                .text(lost.getText())
                .nickname(lost.getUser().getNickname())
                .userThumbnail(lost.getUser().getMyPage().getImg())
                .images(lostImages)
                .comments(lostComments)
                .build();
    }
}
