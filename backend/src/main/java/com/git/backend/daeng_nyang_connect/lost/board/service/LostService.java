package com.git.backend.daeng_nyang_connect.lost.board.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.git.backend.daeng_nyang_connect.exception.FileUploadFailedException;
import com.git.backend.daeng_nyang_connect.lost.board.dto.LostBoardDTO;
import com.git.backend.daeng_nyang_connect.lost.board.dto.LostBoardDetailDTO;
import com.git.backend.daeng_nyang_connect.lost.board.entity.Lost;
import com.git.backend.daeng_nyang_connect.lost.board.entity.LostImage;
import com.git.backend.daeng_nyang_connect.lost.board.repository.LostImgRepository;
import com.git.backend.daeng_nyang_connect.lost.board.repository.LostRepository;
import com.git.backend.daeng_nyang_connect.lost.comments.dto.LostCommentsDTO;
import com.git.backend.daeng_nyang_connect.lost.comments.entity.LostComments;
import com.git.backend.daeng_nyang_connect.lost.comments.repository.LostCommentsRepository;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import com.git.backend.daeng_nyang_connect.user.service.UserService;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Cacheable
public class LostService {
    private final LostRepository lostRepository;
    private final UserService userService;
    private final LostImgUpload lostImgUpload;
    private final LostImgRepository lostImgRepository;
    private final LostCommentsRepository lostCommentsRepository;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    //Lost 글 등록
    @Transactional
    public Map<?,?> addLost(LostBoardDetailDTO lostBoardDetailDTO, String token, List<MultipartFile> img) {
        User user = userService.checkUserByToken(token);

        Lost lost = Lost.builder()
                .lostBoardId(lostBoardDetailDTO.getBoardId())
                .user(user)
                .category(lostBoardDetailDTO.getCategory())
                .place(lostBoardDetailDTO.getPlace())
                .reward(lostBoardDetailDTO.getReward())
                .mobile(lostBoardDetailDTO.getMobile())
                .kind(lostBoardDetailDTO.getKind())
                .breed(lostBoardDetailDTO.getBreed())
                .gender(lostBoardDetailDTO.getGender())
                .color(lostBoardDetailDTO.getColor())
                .lostDate(lostBoardDetailDTO.getLostDate())
                .lostTime(lostBoardDetailDTO.getLostTime())
                .createdAt(timestamp)
                .text(lostBoardDetailDTO.getText())
                .build();
        lostRepository.save(lost);
        lostImgUpload.uploadLostImgs(lost, lostBoardDetailDTO.getText(), img);

        Map<String, String> response = new HashMap<>();
        response.put("msg","게시물이 등록되었습니다");
        response.put("http_status", HttpStatus.CREATED.toString());
        return response;
    }

    //내가 쓴 lost 인지 확인
    public Lost checkMyBoard(Long lostboardId, String token){
        User user = userService.checkUserByToken(token);
        Lost board = lostRepository.findById(lostboardId)
                .orElseThrow(()->new NotFoundException("없는 게시물입니다"));
        List<Lost> byUser = lostRepository.findByUser(user);

        if (byUser.contains(board)){
            return board;
        } else {
            return null;
        }
    }

    //lost 삭제
    public Map<String, String> deleteLost(String token, Long lostBoardId) {
        Lost lost = checkMyBoard(lostBoardId, token);

        lostRepository.delete(lost);
        Map<String,String> response = new HashMap<>();
        response.put("msg","게시물이 삭제되었습니다");
        response.put("http_status", HttpStatus.OK.toString());
        return response;
    }

    //lost 수정
    public Map<String,String> modifyLost(String token, Long lostBoardId,
                                         LostBoardDetailDTO lostBoardDetailDTO)
            throws FileUploadFailedException {
        Lost lost = checkMyBoard(lostBoardId, token);

        lost.setCategory(lostBoardDetailDTO.getCategory());
        lost.setText(lostBoardDetailDTO.getText());
        lost.setLostDate(lostBoardDetailDTO.getLostDate());
        lost.setLostTime(lostBoardDetailDTO.getLostTime());
        lost.setKind(lostBoardDetailDTO.getKind());
        lost.setBreed(lostBoardDetailDTO.getBreed());
        lost.setPlace(lostBoardDetailDTO.getPlace());
        lost.setMobile(lostBoardDetailDTO.getMobile());
        lost.setGender(lostBoardDetailDTO.getGender());
        lost.setColor(lostBoardDetailDTO.getColor());
        lost.setReward(lostBoardDetailDTO.getReward());
        lost.setCreatedAt(timestamp);
        lostRepository.save(lost);

        Map<String,String> response = new HashMap<>();
        response.put("msg", "게시글 수정 완료");
        response.put("http_status", HttpStatus.OK.toString());
        return response;
    }

    //모든 lost 가져오기
    @Transactional
    public List<LostBoardDTO> getAll(){
        List<Lost> lostList = lostRepository.findAll();

        return lostList.stream()
                .map(LostBoardDTO::fromEntity)
                .collect(Collectors.toList());
    }

    //하나의 lost 가져오기
    @Transactional
    public LostBoardDetailDTO getThis(Long lostBoardId){
        Lost thisLost = lostRepository.findById(lostBoardId).orElseThrow();
        List<LostImage> thisLostImg = lostImgRepository.findByLost(thisLost).orElseThrow();
        List<LostComments> thisLostComments = lostCommentsRepository.findByLost(thisLost);

        List<LostCommentsDTO> lostCommentsDTOList = thisLostComments.stream()
                .map(lostComments -> LostCommentsDTO.builder()
                        .commentsId(lostComments.getLostCommentsId())
                        .boardId(lostComments.getLost().getLostBoardId())
                        .userId(lostComments.getUser().getUserId())
                        .nickname(lostComments.getUser().getNickname())
                        .userThumbnail(lostComments.getUser().getMyPage().getImg())
                        .comment(lostComments.getComment())
                        .createdAt(lostComments.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        return LostBoardDetailDTO.fromEntity(thisLost, thisLostImg, lostCommentsDTOList);
    }

    //lost 검색
    public List<LostBoardDTO> searchLost(String keyword) {
        List<Lost> byTextContaining = lostRepository.findByTextContaining(keyword);
        return byTextContaining.stream()
                .map(LostBoardDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Map<String, Integer> getSize(){
        Map<String, Integer> response = new HashMap<>();
        Integer size = lostRepository.findAll().size();
        response.put("size", size);
        return response;
    }
}
