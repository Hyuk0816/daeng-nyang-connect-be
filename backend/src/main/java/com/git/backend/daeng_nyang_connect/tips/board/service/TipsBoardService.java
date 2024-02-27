package com.git.backend.daeng_nyang_connect.tips.board.service;


import com.amazonaws.services.kms.model.NotFoundException;
import com.git.backend.daeng_nyang_connect.exception.FileUploadFailedException;
import com.git.backend.daeng_nyang_connect.notify.service.NotificationService;
import com.git.backend.daeng_nyang_connect.tips.board.dto.TipsBoardDetailDto;
import com.git.backend.daeng_nyang_connect.tips.board.dto.TipsBoardDto;
import com.git.backend.daeng_nyang_connect.tips.board.dto.TipsBoardLikeDto;
import com.git.backend.daeng_nyang_connect.tips.board.entity.Tips;
import com.git.backend.daeng_nyang_connect.tips.board.entity.TipsBoardLike;
import com.git.backend.daeng_nyang_connect.tips.board.entity.TipsImage;
import com.git.backend.daeng_nyang_connect.tips.board.repository.TipsBoardLikeRepository;
import com.git.backend.daeng_nyang_connect.tips.board.repository.TipsBoardRepository;
import com.git.backend.daeng_nyang_connect.tips.board.repository.TipsImageRepository;
import com.git.backend.daeng_nyang_connect.tips.comments.dto.TipsCommentsDto;
import com.git.backend.daeng_nyang_connect.tips.comments.entity.TipsComments;
import com.git.backend.daeng_nyang_connect.tips.comments.repository.TipsCommentsLikeRepository;
import com.git.backend.daeng_nyang_connect.tips.comments.repository.TipsCommentsRepository;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import com.git.backend.daeng_nyang_connect.user.service.UserService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TipsBoardService {
    private final TipsBoardRepository tipsBoardRepository;
    private final TipsImgUpload tipsImgUpload;
    private final TipsBoardLikeRepository tipsBoardLikeRepository;
    private final UserService userService;
    private final TipsImageRepository tipsImageRepository;
    private final TipsCommentsRepository tipsCommentsRepository;
    private final TipsCommentsLikeRepository tipsCommentsLikeRepository;
    private final NotificationService notificationService;

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    //게시글 등록
    @Transactional
    public Map<?,?> postBoard(TipsBoardDto tipsBoardDto, String token, List<MultipartFile> img){

        User user = userService.checkUserByToken(token);

        Tips tips = Tips.builder()
                .tipsBoardId(tipsBoardDto.getBoardId())
                .user(user)
                .category(tipsBoardDto.getCategory())
                .title(tipsBoardDto.getTitle())
                .text(tipsBoardDto.getText())
                .createdAt(timestamp)
                .tipsLike(0)
                .build();
        tipsBoardRepository.save(tips);
        tipsImgUpload.uploadTipsImgs(tips, tipsBoardDto.getTitle(), img);
        Map<String, String> response = new HashMap<>();
        response.put("msg", "게시물이 등록 되었습니다");
        response.put("http_status", HttpStatus.CREATED.toString());
        return response;
        }

    //게시물 ID에 해당하는 user 닉네임 가져오기
    public String  findUserNicknameByTipsId(Long tipsId) {
        Tips tips = tipsBoardRepository.findById(tipsId).orElse(null);
        if (tips != null && tips.getUser() != null) {
            return tips.getUser().getNickname();
        } else {
            return null;
        }
    }

    //자기가 쓴 게시물인지 확인
    public Tips checkMyBoard(Long tipsId, String token){

        User user = userService.checkUserByToken(token);

        Tips board = tipsBoardRepository.findById(tipsId)
                .orElseThrow(() -> new NotFoundException("없는 게시물 입니다"));

        List<Tips> byUser = tipsBoardRepository.findByUser(user);

        if(byUser.contains(board)){
            return board;
        }else{
            return null;
        }
    }
    //좋아요 로직
    public void setHeart(Tips tips, User user, Integer likeCount, Boolean msg){
        if(msg){
            TipsBoardLike tipsBoardLike = new TipsBoardLike(tips, user);
            likeCount++;
            tips.setTipsLike(likeCount);
            tipsBoardLikeRepository.save(tipsBoardLike);
            tipsBoardRepository.save(tips);
            notifyPostLike(tips);
        }else{
            tipsBoardLikeRepository.deleteByUserAndTips(user,tips);
            likeCount--;
            tips.setTipsLike(likeCount);
            tipsBoardRepository.save(tips);
        }
    }
    //좋아요 클릭 로직
    @Transactional
    public ResponseEntity<String> clickLike(Long tipsId, String token){

        User user = userService.checkUserByToken(token);
        Tips isTips = tipsBoardRepository.findById(tipsId)
                .orElseThrow();


        if(tipsBoardLikeRepository.findByTipsAndUser(isTips,user)==null){
            setHeart(isTips, user, isTips.getTipsLike(), true);
            return ResponseEntity.ok().body(tipsId + "번 게시글에 좋아요가 추가 되었습니다");
            }
        else{
            setHeart(isTips, user, isTips.getTipsLike(), false);
            return ResponseEntity.ok().body(tipsId + "번 게시글에 좋아요가 취소 되었습니다");
            }
    }
    // 좋아요 알림
    private void notifyPostLike(Tips tips) {
        notificationService.notifyPostLike(tips.getTipsBoardId(), "댕냥꿀팁");
    }
    //게시물 삭제
    public Map<String,String> delete(String token, Long tipsId) {

        Tips tips = checkMyBoard(tipsId, token);

        tipsBoardRepository.delete(tips);
        Map<String,String> response = new HashMap<>();
        response.put("msg","게시물이 삭제 되었습니다");
        response.put("http_status", HttpStatus.OK.toString());
        return response;
    }

    //게시글 수정
    public Map<String ,String> modifyTips(String token, Long tipsId, TipsBoardDto tipsBoardDto,
                                          Long tipsImgId,MultipartFile multipartFile) throws FileUploadFailedException {

        Tips tips = checkMyBoard(tipsId, token);


        TipsImage byId = tipsImageRepository.findById(tipsImgId)
                .orElseThrow();

        tips.setCategory(tipsBoardDto.getCategory());
        tips.setTitle(tipsBoardDto.getTitle());
        tips.setText(tipsBoardDto.getText());
        tips.setCreatedAt(timestamp);
        byId.setUrl(tipsImgUpload.uploadModifyTipsImg(tips, tips.getTitle(), multipartFile));
        tipsBoardRepository.save(tips);
        tipsImageRepository.save(byId);


        Map<String, String> response = new HashMap<>();
        response.put("msg", "게시글 수정 완료 되었습니다");
        response.put("http_status", HttpStatus.OK.toString());
        return response;
    }
    //게시글 수정
    public Map<String ,String> modifyTipsNoImg(String token, Long tipsId, TipsBoardDto tipsBoardDto) {

        Tips tips = checkMyBoard(tipsId, token);
        tips.setCategory(tipsBoardDto.getCategory());
        tips.setTitle(tipsBoardDto.getTitle());
        tips.setText(tipsBoardDto.getText());
        tips.setCreatedAt(timestamp);
        tipsBoardRepository.save(tips);

        Map<String, String> response = new HashMap<>();
        response.put("msg", "게시글 수정 완료 되었습니다");
        response.put("http_status", HttpStatus.OK.toString());
        return response;
    }
    //  게시글, 닉네임, 작성 시간 좋아요만 보이면 됨
    @Cacheable(cacheNames = "tips_Board_All", key = "#pageable.pageNumber")
    public List<TipsBoardDto> getAll(Pageable pageable){
        Pageable customPageable = PageRequest.of(pageable.getPageNumber(), 5, pageable.getSort());
        Page<Tips> tipsPage = tipsBoardRepository.findAll(customPageable);
        List<Tips> tipsList = tipsPage.getContent();

        return tipsList.stream()
                .map(tips -> {
                    String author = findUserNicknameByTipsId(tips.getTipsBoardId());
                    return TipsBoardDto.fromEntity(tips, author);
                })
                .collect(Collectors.toList());
    }


    public List<TipsBoardDto> searchBoard(String keyword){

        List<Tips> byTitleContaining = tipsBoardRepository.findByTitleContaining(keyword);
        Integer size = tipsBoardRepository.findByTitleContaining(keyword).size();
        return byTitleContaining.stream().map(tips -> {
           String author = findUserNicknameByTipsId(tips.getTipsBoardId());
           return TipsBoardDto.fromEntity(tips, author);
        }).collect(Collectors.toList());

    }
    @Cacheable(cacheNames = "tips_Board_Detail", key = "#tipsID")
    public TipsBoardDetailDto getThisBoard(Long tipsID){

        Tips thisBoard = tipsBoardRepository.findById(tipsID).orElseThrow();
        List<TipsImage> thisBoardImg = tipsImageRepository.findByTips(thisBoard).orElseThrow();
        List<TipsComments> thisBoardComments = tipsCommentsRepository.findByTips(thisBoard);
        List<TipsBoardLike> tipsBoardLikes = tipsBoardLikeRepository.findByTips(thisBoard);

        // 필요한 정보만을 DTO로 변환
        List<TipsCommentsDto> tipsCommentsDtoList = thisBoardComments.stream()
                .map(comment -> TipsCommentsDto.builder()
                        .commentsId(comment.getTipsCommentsId())
                        .boardId(comment.getTips().getTipsBoardId())
                        .userId(comment.getUser().getUserId())
                        .userThumbnail(comment.getUser().getMyPage().getImg())
                        .nickname(comment.getUser().getNickname())
                        .comment(comment.getComment())
                        .like(comment.getTipsCommentsLike())
                        .createdAt(comment.getCreatedAt())
                        .likes(comment.getLikeList())
                        .build())
                .collect(Collectors.toList());

        List<TipsBoardLikeDto> tipsBoardLikeDtos = tipsBoardLikes.stream()
                .map(like -> TipsBoardLikeDto.builder()
                        .likeId(like.getTipsBoardLikeId())
                        .userId(like.getUser().getUserId())
                        .build())
                .toList();

        return TipsBoardDetailDto.fromEntity(thisBoard, thisBoardImg, tipsCommentsDtoList,tipsBoardLikeDtos);

    }
    public Map<String, Integer> getSize(){
        Map<String, Integer> response = new HashMap<>();
        Integer size = tipsBoardRepository.findAll().size();
        response.put("size", size);
        return response;
    }

}
