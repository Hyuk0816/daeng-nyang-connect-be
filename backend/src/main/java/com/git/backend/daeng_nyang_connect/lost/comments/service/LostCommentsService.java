package com.git.backend.daeng_nyang_connect.lost.comments.service;

import com.git.backend.daeng_nyang_connect.lost.board.entity.Lost;
import com.git.backend.daeng_nyang_connect.lost.board.repository.LostRepository;
import com.git.backend.daeng_nyang_connect.lost.comments.dto.LostCommentsDTO;
import com.git.backend.daeng_nyang_connect.lost.comments.entity.LostComments;
import com.git.backend.daeng_nyang_connect.lost.comments.repository.LostCommentsRepository;
import com.git.backend.daeng_nyang_connect.notify.service.NotificationService;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import com.git.backend.daeng_nyang_connect.user.service.UserService;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Cacheable
public class LostCommentsService {
    private final UserService userService;
    private final LostRepository lostRepository;
    private final LostCommentsRepository lostCommentsRepository;
    private final NotificationService notificationService;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    //댓글 작성
    public Map<?,?> lostCommentAdd(String token, Long lostBoardId, LostCommentsDTO lostCommentsDTO){
        User user = userService.checkUserByToken(token);
        Lost byId = lostRepository.findById(lostBoardId).orElseThrow();

        LostComments lostComments = LostComments.builder()
                .lostCommentsId(lostCommentsDTO.getCommentsId())
                .comment(lostCommentsDTO.getComment())
                .lost(byId)
                .createdAt(timestamp)
                .user(user)
                .build();

        lostCommentsRepository.save(lostComments);
        notificationService.notifyComment(lostBoardId,  "댕냥 미아센터");

        Map<String, String> response = new HashMap<>();
        response.put("msg", "댓글이 등록되었습니다.");
        response.put("http_status", HttpStatus.OK.toString());
        return response;
    }

    //내가 쓴 댓글인지 확인
    public LostComments checkMyComment(Long lostCommentId, String token){
        User user = userService.checkUserByToken(token);
        LostComments lostComments = lostCommentsRepository.findById(lostCommentId)
                .orElseThrow();
        List<LostComments> byUser = lostCommentsRepository.findByUser(user);
        if (byUser.contains(lostComments)){
            return lostComments;
        }else {
            return null;
        }
    }

    //댓글 수정
    public Map<?,?> lostCommentModify(String token, Long lostCommentsId, LostCommentsDTO lostCommentsDTO){
        LostComments lostComments = checkMyComment(lostCommentsId, token);

        lostComments.setComment(lostCommentsDTO.getComment());
        lostComments.setCreatedAt(timestamp);

        lostCommentsRepository.save(lostComments);

        Map<String, String> response = new HashMap<>();
        response.put("msg","댓글 수정 완료");
        response.put("http_status",HttpStatus.OK.toString());
        return response;
    }

    //댓글 삭제
    public Map<?,?> lostCommentDelete(String token, Long lostCommentsId){
        LostComments lostComments = checkMyComment(lostCommentsId, token);
        lostCommentsRepository.delete(lostComments);
        Map<String, String> response = new HashMap<>();
        response.put("msg", "댓글 삭제 완료");
        response.put("http_status", HttpStatus.OK.toString());
        return response;
    }
}
