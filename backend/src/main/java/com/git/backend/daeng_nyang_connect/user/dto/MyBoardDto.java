package com.git.backend.daeng_nyang_connect.user.dto;

import com.git.backend.daeng_nyang_connect.lost.board.dto.LostBoardDTO;
import com.git.backend.daeng_nyang_connect.lost.board.entity.Lost;
import com.git.backend.daeng_nyang_connect.mate.board.dto.MateDTO;
import com.git.backend.daeng_nyang_connect.mate.board.entity.Mate;
import com.git.backend.daeng_nyang_connect.mypet.board.dto.MyPetDTO;
import com.git.backend.daeng_nyang_connect.mypet.board.entity.MyPet;
import com.git.backend.daeng_nyang_connect.review.board.dto.FindReviewDto;
import com.git.backend.daeng_nyang_connect.review.board.dto.request.ReviewRequestDTO;
import com.git.backend.daeng_nyang_connect.review.board.entity.Review;
import com.git.backend.daeng_nyang_connect.tips.board.dto.TipsBoardDto;
import com.git.backend.daeng_nyang_connect.tips.board.entity.Tips;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyBoardDto {

    private List<TipsBoardDto> tips;
    private List<MateDTO> mate;
    private List<LostBoardDTO> lost;
    private List<MyPetDTO> myPet;
    private List<FindReviewDto> review;


    public static MyBoardDto fromEntity(List<TipsBoardDto> tips, List<MateDTO> mates, List<LostBoardDTO> lost, List<FindReviewDto> reviewList, List<MyPetDTO> myPet){
        return MyBoardDto.builder()
                .tips(tips)
                .mate(mates)
                .lost(lost)
                .review(reviewList)
                .myPet(myPet)
                .build();
    }




}
