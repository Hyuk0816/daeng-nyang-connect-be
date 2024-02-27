package com.git.backend.daeng_nyang_connect.mypet.comments.service;

import com.git.backend.daeng_nyang_connect.config.jwt.TokenProvider;
import com.git.backend.daeng_nyang_connect.mypet.board.entity.MyPet;
import com.git.backend.daeng_nyang_connect.mypet.board.repository.MyPetRepository;
import com.git.backend.daeng_nyang_connect.mypet.comments.dto.MyPetCommentsDTO;
import com.git.backend.daeng_nyang_connect.mypet.comments.entity.MyPetComments;
import com.git.backend.daeng_nyang_connect.mypet.comments.entity.MyPetCommentsLike;
import com.git.backend.daeng_nyang_connect.mypet.comments.repository.MyPetCommentsLikeRepository;
import com.git.backend.daeng_nyang_connect.mypet.comments.repository.MyPetCommentsRepository;
import com.git.backend.daeng_nyang_connect.notify.service.NotificationService;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import com.git.backend.daeng_nyang_connect.user.repository.UserRepository;
import jakarta.persistence.Cacheable;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Cacheable
public class MyPetCommentsService {

    private final TokenProvider tokenProvider;
    private final MyPetRepository myPetRepository;
    private final MyPetCommentsRepository myPetCommentsRepository;
    private final MyPetCommentsLikeRepository myPetCommentsLikeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private static final String MSG_USER_NOT_FOUND = "유저를 찾을 수 없습니다.";
    private static final String MSG_COMMENT_NOT_FOUND = "댓글을 찾을 수 없습니다.";
    private static final String MSG_OWNER_ACCESS_DENIED = "댓글의 소유자가 아닙니다.";

    @Transactional
    public Map<String, String> postComment(String token,Long myPet, MyPetCommentsDTO myPetCommentsDTO ) {
        try {
            User user = userRepository.findByEmail(tokenProvider.getEmailBytoken(token))
                    .orElseThrow(() -> new NoSuchElementException(MSG_USER_NOT_FOUND));

            MyPet byId = myPetRepository.findById(myPet).orElseThrow();

            MyPetComments myPetComments = MyPetComments.builder()
                    .myPetCommentsId(myPetCommentsDTO.getCommentsId())
                    .comment(myPetCommentsDTO.getComment())
                    .myPet(byId)
                    .createdAt(myPetCommentsDTO.getCreatedAt())
                    .user(user)
                    .myPetCommentsLike(0)
                    .build();

            myPetCommentsRepository.save(myPetComments);
            notificationService.notifyComment(myPet, "나의 댕냥이");

            return createSuccessResponse("댓글이 등록되었습니다.", HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return createErrorResponse(MSG_USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return createErrorResponse("댓글 등록 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, String> modifyComment(String token, Long myPetCommentsId, MyPetCommentsDTO myPetCommentsDTO) {

        try {
            User user = userRepository.findByEmail(tokenProvider.getEmailBytoken(token))
                    .orElseThrow(() -> new EntityNotFoundException(MSG_USER_NOT_FOUND));

            MyPetComments myPetComments = myPetCommentsRepository.findById(myPetCommentsId)
                    .orElseThrow(() -> new EntityNotFoundException(MSG_COMMENT_NOT_FOUND));

            checkOwnership(myPetComments, user);
            modifyMyPetCommentsFields(myPetComments, myPetCommentsDTO);
            myPetCommentsRepository.save(myPetComments);

            return createSuccessResponse("댓글이 수정되었습니다.", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return createErrorResponse(MSG_COMMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return createErrorResponse(MSG_OWNER_ACCESS_DENIED, HttpStatus.FORBIDDEN);
        }
    }

    @Transactional
    public Map<String, String> deleteComment(String token, Long myPetCommentsId) {
        try {
            User user = userRepository.findByEmail(tokenProvider.getEmailBytoken(token))
                    .orElseThrow(() -> new EntityNotFoundException(MSG_USER_NOT_FOUND));

            MyPetComments myPetComments = myPetCommentsRepository.findById(myPetCommentsId)
                    .orElseThrow(() -> new EntityNotFoundException(MSG_COMMENT_NOT_FOUND));

            checkOwnership(myPetComments, user);
            myPetCommentsRepository.delete(myPetComments);

            return createSuccessResponse("댓글이 삭제되었습니다.", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return createErrorResponse(MSG_COMMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return createErrorResponse(MSG_OWNER_ACCESS_DENIED, HttpStatus.FORBIDDEN);
        }
    }

    @Transactional
    public void setHeart(MyPetComments myPetComments, User user, Boolean msg) {
        boolean hasUserLiked = myPetCommentsLikeRepository.findByMyPetCommentsAndUser(myPetComments, user).isPresent();

        if (msg) {
            // 좋아요 추가
            if (!hasUserLiked) {
                MyPetCommentsLike myPetCommentsLike = new MyPetCommentsLike(myPetComments, user);
                myPetComments.getMyPetCommentsLikes().add(myPetCommentsLike);
                myPetComments.setMyPetCommentsLike(myPetComments.getMyPetCommentsLike() + 1);
                myPetCommentsRepository.save(myPetComments);
                notifyCommentLike(myPetComments);
            }
        } else {
            // 좋아요 취소
            if (hasUserLiked) {
                MyPetCommentsLike userLike = myPetCommentsLikeRepository.findByMyPetCommentsAndUser(myPetComments, user)
                        .orElseThrow(() -> new RuntimeException("사용자의 좋아요가 해당 댓글에 없습니다."));
                myPetComments.getMyPetCommentsLikes().remove(userLike);
                myPetCommentsLikeRepository.delete(userLike);
                // 음수가 되지 않도록 확인
                int likeCount = myPetComments.getMyPetCommentsLike();
                if (likeCount > 0) {
                    myPetComments.setMyPetCommentsLike(likeCount - 1);
                    myPetCommentsRepository.save(myPetComments);
                }
            }
        }
    }
    //좋아요 클릭
    @Transactional
    public Map<String, String> clickLike(Long myPetCommentsId, String token){

        User user = userRepository.findByEmail(tokenProvider.getEmailBytoken(token))
                .orElseThrow(() -> new EntityNotFoundException(MSG_USER_NOT_FOUND));

        MyPetComments myPetComments = myPetCommentsRepository.findById(myPetCommentsId)
                .orElseThrow(() -> new EntityNotFoundException(MSG_COMMENT_NOT_FOUND));

        // 이미 좋아요를 눌렀는지 확인
        boolean hasUserLiked = myPetCommentsLikeRepository.findByMyPetCommentsAndUser(myPetComments, user).isPresent();

        if (!hasUserLiked) {
            // 좋아요 추가
            setHeart(myPetComments, user, true);
            return createSuccessResponse(myPetCommentsId + "번 댓글에 좋아요가 추가되었습니다.", HttpStatus.OK);
        } else {
            // 좋아요 취소
            setHeart(myPetComments, user, false);
            return createSuccessResponse(myPetCommentsId + "번 댓글에 좋아요가 취소되었습니다.", HttpStatus.OK);
        }
    }

    private void notifyCommentLike(MyPetComments myPetComments) {
        notificationService.notifyCommentLike(myPetComments.getMyPetCommentsId(), "나의 댕냥이");
    }

    private Map<String, String> createSuccessResponse(String message, HttpStatus httpStatus) {
        Map<String, String> response = new HashMap<>();
        response.put("msg", message);
        response.put("http_status", httpStatus.toString());
        return response;
    }
    private Map<String, String> createErrorResponse(String message, HttpStatus httpStatus) {
        Map<String, String> response = new HashMap<>();
        response.put("error_msg", message);
        response.put("http_status", httpStatus.toString());
        return response;
    }

    private void checkOwnership(MyPetComments myPetComments, User user) {
        if (!myPetComments.getUser().equals(user)) {
            throw new AccessDeniedException(MSG_OWNER_ACCESS_DENIED);
        }
    }

    private void modifyMyPetCommentsFields(MyPetComments myPetComments, MyPetCommentsDTO myPetCommentsDTO) {
        myPetComments.setComment(myPetCommentsDTO.getComment());
    }
}