package com.git.backend.daeng_nyang_connect.tips.board.dto;

import com.git.backend.daeng_nyang_connect.tips.board.entity.Tips;
import com.git.backend.daeng_nyang_connect.tips.board.entity.TipsImage;
import com.git.backend.daeng_nyang_connect.tips.comments.dto.TipsCommentsDto;
import com.git.backend.daeng_nyang_connect.tips.comments.dto.TipsCommentsLikeDto;
import com.git.backend.daeng_nyang_connect.tips.comments.entity.TipsComments;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TipsBoardDetailDto {

    private Long boardId;
    private String category;
    private String title;
    private String text;
    private List<TipsImage> img;
    private Integer like;
    private Timestamp createdAt;

    //게시글 작성 유저 닉네임 필요
    private String nickname;
    private String userThumbnail;

    private List<TipsCommentsDto> comments;
    private List<TipsBoardLikeDto> likes;



    public static TipsBoardDetailDto fromEntity(Tips tips, List<TipsImage> tipsImage, List<TipsCommentsDto> comments,
                                                 List<TipsBoardLikeDto> likes) {
        return TipsBoardDetailDto.builder()
                .boardId(tips.getTipsBoardId())
                .category(tips.getCategory())
                .title(tips.getTitle())
                .text(tips.getText())
                .img(tipsImage)
                .like(tips.getTipsLike())
                .createdAt(tips.getCreatedAt())
                .nickname(tips.getUser().getNickname())
                .userThumbnail(tips.getUser().getMyPage().getImg())
                .comments(comments)
                .likes(likes)
                .build();
    }


}
