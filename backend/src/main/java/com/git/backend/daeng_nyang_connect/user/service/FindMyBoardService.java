package com.git.backend.daeng_nyang_connect.user.service;

import com.git.backend.daeng_nyang_connect.animal.repository.AnimalRepository;
import com.git.backend.daeng_nyang_connect.lost.board.dto.LostBoardDTO;
import com.git.backend.daeng_nyang_connect.lost.board.entity.Lost;
import com.git.backend.daeng_nyang_connect.lost.board.repository.LostRepository;
import com.git.backend.daeng_nyang_connect.mate.board.dto.MateDTO;
import com.git.backend.daeng_nyang_connect.mate.board.entity.Mate;
import com.git.backend.daeng_nyang_connect.mate.board.entity.MateBoardLike;
import com.git.backend.daeng_nyang_connect.mate.board.repository.MateBoardLikeRepository;
import com.git.backend.daeng_nyang_connect.mate.board.repository.MateRepository;
import com.git.backend.daeng_nyang_connect.mypet.board.dto.MyPetDTO;
import com.git.backend.daeng_nyang_connect.mypet.board.entity.MyPet;
import com.git.backend.daeng_nyang_connect.mypet.board.entity.MyPetBoardLike;
import com.git.backend.daeng_nyang_connect.mypet.board.repository.MyPetBoardLikeRepository;
import com.git.backend.daeng_nyang_connect.mypet.board.repository.MyPetRepository;
import com.git.backend.daeng_nyang_connect.review.board.dto.FindReviewDto;
import com.git.backend.daeng_nyang_connect.review.board.dto.request.ReviewRequestDTO;
import com.git.backend.daeng_nyang_connect.review.board.entity.Review;
import com.git.backend.daeng_nyang_connect.review.board.entity.ReviewLike;
import com.git.backend.daeng_nyang_connect.review.board.repository.ReviewLikeRepository;
import com.git.backend.daeng_nyang_connect.review.board.repository.ReviewRepository;
import com.git.backend.daeng_nyang_connect.tips.board.dto.TipsBoardDto;
import com.git.backend.daeng_nyang_connect.tips.board.dto.TipsBoardLikeDto;
import com.git.backend.daeng_nyang_connect.tips.board.entity.Tips;
import com.git.backend.daeng_nyang_connect.tips.board.entity.TipsBoardLike;
import com.git.backend.daeng_nyang_connect.tips.board.repository.TipsBoardLikeRepository;
import com.git.backend.daeng_nyang_connect.tips.board.repository.TipsBoardRepository;
import com.git.backend.daeng_nyang_connect.user.dto.MyBoardDto;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import com.git.backend.daeng_nyang_connect.user.repository.UserRepository;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Cacheable
public class FindMyBoardService {

    private final UserService userService;
    private final TipsBoardRepository tipsBoardRepository;
    private final LostRepository lostRepository;
    private final ReviewRepository reviewRepository;
    private final MyPetRepository myPetRepository;
    private final MateRepository mateRepository;

    private final TipsBoardLikeRepository tipsBoardLikeRepository;
    private final MateBoardLikeRepository mateBoardLikeRepository;
    private final MyPetBoardLikeRepository myPetBoardLikeRepository;
    private final ReviewLikeRepository reviewLikeRepository;





    /**
     *
     *  내가 쓴 커뮤니티 글을 조회하는 API
     * @param token
     * @param myBoardDto
     * @return
     */

    public MyBoardDto getMyBoard(String token){
        User user = userService.checkUserByToken(token);

        List<Tips> tips = tipsBoardRepository.findByUser(user);
        List<Lost> lost = lostRepository.findByUser(user);
        List<Review> review = reviewRepository.findByUserNickname(user.getNickname());
        List<MyPet> myPets = myPetRepository.findByUserEmail(user.getEmail());
        List<Mate> mates = mateRepository.findByUserEmail(user.getEmail());


        List<TipsBoardDto> tipsBoardDtoList = tips.stream().
                map(tipsDto -> TipsBoardDto.builder()
                        .boardName("Tips")
                        .boardId(tipsDto.getTipsBoardId())
                        .title(tipsDto.getTitle())
                        .text(tipsDto.getText())
                        .createdAt(tipsDto.getCreatedAt())
                        .build()).toList();

        List<FindReviewDto> reviewRequestDTOS = review.stream().
                map(reviewDto -> FindReviewDto.builder()
                        .boardName("Review")
                        .boardId(reviewDto.getReviewId())
                        .text(reviewDto.getTextReview())
                        .createdAt(reviewDto.getCreatedAt())
                        .build()).toList();

        List<MyPetDTO> myPetDTOList = myPets.stream().
                map(myPet -> MyPetDTO.builder()
                        .boardName("MyPet")
                        .boardId(myPet.getMyPetBoardId())
                        .text(myPet.getText())
                        .createdAt(myPet.getCreatedAt())
                        .build()).toList();

        List<MateDTO> mateDTOList = mates.stream()
                .map(mate -> MateDTO.builder()
                        .boardName("Mate")
                        .boardId(mate.getMateBoardId())
                        .text(mate.getText())
                        .createdAt(mate.getCreatedAt())
                        .build()).toList();

        List<LostBoardDTO> lostDto = lost.stream()
                .map(lostList -> LostBoardDTO.builder()
                        .boardName("Lost")
                        .boardId(lostList.getLostBoardId())
                        .text(lostList.getText())
                        .createdAt(lostList.getCreatedAt())
                        .build()).toList();

        return MyBoardDto.fromEntity(tipsBoardDtoList,mateDTOList,lostDto,reviewRequestDTOS,myPetDTOList);
    }

    public List<Object> getLikeBoard(String token){
        User user = userService.checkUserByToken(token);
        List<Object> likedItems = new ArrayList<>();


        List<TipsBoardLike> tipsLikes = tipsBoardLikeRepository.findAllByUser(user);
        List<TipsBoardDto> tipsBoardDtoList = new ArrayList<>();

        List<MateBoardLike> mateBoardLikes = mateBoardLikeRepository.findAllByUser(user);
        List<MateDTO> mateDTOList = new ArrayList<>();

        List<MyPetBoardLike> myPetBoardLikes = myPetBoardLikeRepository.findAllByUser(user);
        List<MyPetDTO> myPetDTOList = new ArrayList<>();

        List<ReviewLike> reviewBoardLikes = reviewLikeRepository.findAllByUser(user);
        List<FindReviewDto> reviewDtoList = new ArrayList<>();


        for (TipsBoardLike tipsLike : tipsLikes) {
            Tips byId = tipsBoardRepository.findById(tipsLike.getTips().getTipsBoardId()).orElseThrow();
            TipsBoardDto dto = TipsBoardDto.builder()
                    .boardName("Tips")
                    .boardId(byId.getTipsBoardId())
                    .title(byId.getTitle())
                    .category(byId.getCategory())
                    .text(byId.getText())
                    .build();
            tipsBoardDtoList.add(dto);
        }

        for (MateBoardLike mateLike : mateBoardLikes) {
            Mate byId = mateRepository.findById(mateLike.getMate().getMateBoardId()).orElseThrow();
            MateDTO dto = MateDTO.builder()
                    .boardName("Mate")
                    .boardId(byId.getMateBoardId())
                    .text(byId.getText())
                    .build();
            mateDTOList.add(dto);
        }

        for (MyPetBoardLike myPetLike : myPetBoardLikes) {
            MyPet byId = myPetRepository.findById(myPetLike.getMyPet().getMyPetBoardId()).orElseThrow();
            MyPetDTO dto = MyPetDTO.builder()
                    .boardName("MyPet")
                    .boardId(byId.getMyPetBoardId())
                    .text(byId.getText())  // 여기서 text로 변경
                    .build();
            myPetDTOList.add(dto);
        }
        for(ReviewLike reviewLike : reviewBoardLikes){
            Review review = reviewRepository.findById(reviewLike.getReview().getReviewId()).orElseThrow();
            FindReviewDto dto = FindReviewDto.builder()
                    .boardName("Review")
                    .boardId(review.getReviewId())
                    .text(review.getTextReview())
                    .createdAt(review.getCreatedAt())
                    .build();
            reviewDtoList.add(dto);
        }


        likedItems.addAll(tipsBoardDtoList);
        likedItems.addAll(mateDTOList);
        likedItems.addAll(myPetDTOList);
        likedItems.addAll(reviewDtoList);

        return likedItems;

    }



}
