package com.git.backend.daeng_nyang_connect.tips.comments.service;

import com.git.backend.daeng_nyang_connect.notify.service.NotificationService;
import com.git.backend.daeng_nyang_connect.tips.board.entity.Tips;
import com.git.backend.daeng_nyang_connect.tips.board.repository.TipsBoardRepository;
import com.git.backend.daeng_nyang_connect.tips.board.service.TipsBoardService;
import com.git.backend.daeng_nyang_connect.tips.comments.dto.TipsCommentsDto;
import com.git.backend.daeng_nyang_connect.tips.comments.entity.TipsComments;
import com.git.backend.daeng_nyang_connect.tips.comments.entity.TipsCommentsLike;
import com.git.backend.daeng_nyang_connect.tips.comments.repository.TipsCommentsLikeRepository;
import com.git.backend.daeng_nyang_connect.tips.comments.repository.TipsCommentsRepository;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import com.git.backend.daeng_nyang_connect.user.service.UserService;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Cacheable
public class TipsCommentsService {

    private final TipsBoardRepository tipsBoardRepository;
    private final TipsCommentsLikeRepository tipsCommentsLikeRepository;
    private final TipsCommentsRepository tipsCommentsRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public ResponseEntity<?> postComment(String token, Long tipsId, TipsCommentsDto tipsCommentsDto){

        User user = userService.checkUserByToken(token);
        Tips byId = tipsBoardRepository.findById(tipsId).orElseThrow();

       TipsComments tipsComments = TipsComments.builder()
               .tipsCommentsId(tipsCommentsDto.getCommentsId())
               .comment(tipsCommentsDto.getComment())
               .tips(byId)
               .createdAt(timestamp)
               .user(user)
               .tipsCommentsLike(0)
               .build();

       tipsCommentsRepository.save(tipsComments);
        notificationService.notifyComment(tipsId,  "댕냥꿀팁");

        Map<String, String> response = new HashMap<>();
        response.put("msg", "댓글이 등록 되었습니다");
        response.put("http_status", HttpStatus.OK.toString());
        return ResponseEntity.ok(response);
    }
    //내가 쓴 댓글인지 확인
    public TipsComments checkMyComment(Long tipsCommentId, String token){
        User user = userService.checkUserByToken(token);

        TipsComments tipsComments = tipsCommentsRepository.findById(tipsCommentId)
                .orElseThrow();
        List<TipsComments> byUser = tipsCommentsRepository.findByUser(user);

        if(byUser.contains(tipsComments)){
            return tipsComments;
        }else{


            return null;
        }
    }
    //댓글 수정 기능
    public Map<?,?> modifyComment(String token, Long tipsCommentsId,TipsCommentsDto tipsCommentsDto){

        TipsComments tipsComments = checkMyComment(tipsCommentsId,token);

        tipsComments.setComment(tipsCommentsDto.getComment());
        tipsComments.setCreatedAt(timestamp);

        tipsCommentsRepository.save(tipsComments);

        Map<String, String> response = new HashMap<>();
        response.put("msg", "댓글 수정 완료 되었습니다");
        response.put("http_status", HttpStatus.OK.toString());
        return response;
    }

    public Map<?,?>deleteComment(String token, Long tipsCommentsId){

        TipsComments tipsComments = checkMyComment(tipsCommentsId, token);
        tipsCommentsRepository.delete(tipsComments);
        Map<String, String> response = new HashMap<>();
        response.put("msg", "댓글 삭제 완료 되었습니다");
        response.put("http_status", HttpStatus.OK.toString());
        return response;

    }

    //좋아요 로직
    public void setHeart(TipsComments tipsComments, User user, Integer likeCount, Boolean msg){
        if(msg){
            TipsCommentsLike tipsCommentsLike = new TipsCommentsLike(user,tipsComments);
            likeCount++;
            tipsComments.setTipsCommentsLike(likeCount);
            tipsCommentsLikeRepository.save(tipsCommentsLike);
            tipsCommentsRepository.save(tipsComments);
            notifyCommentLike(tipsComments);
        }else{
            tipsCommentsLikeRepository.deleteByUserAndTipsComments(user, tipsComments);
            likeCount--;
            tipsComments.setTipsCommentsLike(likeCount);
            tipsCommentsRepository.save(tipsComments);
        }
    }
    //좋아요 클릭
    @Transactional
    public Map<?,?> clickLike(Long tipsCommentId, String token){

        User user = userService.checkUserByToken(token);

        TipsComments isTipsComment = tipsCommentsRepository.findById(tipsCommentId)
                .orElseThrow();

        if(tipsCommentsLikeRepository.findByTipsCommentsAndUser(isTipsComment,user)==null){
            setHeart(isTipsComment, user, isTipsComment.getTipsCommentsLike(), true);
            Map<String, String> response = new HashMap<>();
            response.put("msg", "좋아요 완료 되었습니다");
            response.put("http_status", HttpStatus.OK.toString());
            return response;
        }
        else{
            setHeart(isTipsComment, user, isTipsComment.getTipsCommentsLike(), false);
            Map<String, String> response = new HashMap<>();
            response.put("msg", "좋아요 취소 되었습니다");
            response.put("http_status", HttpStatus.OK.toString());
            return response;
        }
    }
    // 댓글 좋아요 알림
    private void notifyCommentLike(TipsComments tipsComments) {
        notificationService.notifyCommentLike(tipsComments.getTipsCommentsId(), "댕냥꿀팁");
    }
}
