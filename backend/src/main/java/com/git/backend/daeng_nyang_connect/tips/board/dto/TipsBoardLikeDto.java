package com.git.backend.daeng_nyang_connect.tips.board.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TipsBoardLikeDto {
    private Long likeId;
    private Long userId;
    private Long boardId;


}
