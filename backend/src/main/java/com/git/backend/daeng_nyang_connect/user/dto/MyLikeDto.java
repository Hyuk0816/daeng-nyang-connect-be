package com.git.backend.daeng_nyang_connect.user.dto;

import com.git.backend.daeng_nyang_connect.mate.board.dto.MateBoardLikeDTO;
import com.git.backend.daeng_nyang_connect.mate.board.dto.MateDTO;
import com.git.backend.daeng_nyang_connect.mypet.board.dto.MyPetBoardLikeDTO;
import com.git.backend.daeng_nyang_connect.mypet.board.dto.MyPetDTO;
import com.git.backend.daeng_nyang_connect.tips.board.dto.TipsBoardDto;
import com.git.backend.daeng_nyang_connect.tips.board.dto.TipsBoardLikeDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyLikeDto {

    private List<TipsBoardDto> tipsBoardLikeDTOS;
    private List<MateDTO> mateBoardLikeDTOS;
    private List<MyPetDTO> myPetBoardLikeDTOS;

    public static MyLikeDto fromEntity(List<TipsBoardDto> tipsBoardDtoList, List<MateDTO> mateDTOList, List<MyPetDTO> myPetDTOList){

        return MyLikeDto.builder()
                .tipsBoardLikeDTOS(tipsBoardDtoList)
                .mateBoardLikeDTOS(mateDTOList)
                .myPetBoardLikeDTOS(myPetDTOList)
                .build();
    }
}
