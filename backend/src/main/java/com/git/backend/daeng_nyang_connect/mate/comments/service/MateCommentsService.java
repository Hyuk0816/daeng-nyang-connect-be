package com.git.backend.daeng_nyang_connect.mate.comments.service;

import com.git.backend.daeng_nyang_connect.config.jwt.TokenProvider;
import com.git.backend.daeng_nyang_connect.mate.board.entity.Mate;
import com.git.backend.daeng_nyang_connect.mate.board.repository.MateRepository;
import com.git.backend.daeng_nyang_connect.mate.comments.dto.MateCommentsDTO;
import com.git.backend.daeng_nyang_connect.mate.comments.entity.MateComments;
import com.git.backend.daeng_nyang_connect.mate.comments.entity.MateCommentsLike;
import com.git.backend.daeng_nyang_connect.mate.comments.repository.MateCommentsLikeRepository;
import com.git.backend.daeng_nyang_connect.mate.comments.repository.MateCommentsRepository;
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
public class MateCommentsService {

    private final TokenProvider tokenProvider;
    private final MateRepository mateRepository;
    private final MateCommentsRepository mateCommentsRepository;
    private final MateCommentsLikeRepository mateCommentsLikeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private static final String MSG_USER_NOT_FOUND = "유저를 찾을 수 없습니다.";
    private static final String MSG_COMMENT_NOT_FOUND = "댓글을 찾을 수 없습니다.";
    private static final String MSG_OWNER_ACCESS_DENIED = "댓글의 소유자가 아닙니다.";

    @Transactional
    public Map<String, String> postComment(String token,Long mate, MateCommentsDTO mateCommentsDTO ) {
        try {
            User user = userRepository.findByEmail(tokenProvider.getEmailBytoken(token))
                    .orElseThrow(() -> new NoSuchElementException(MSG_USER_NOT_FOUND));

            Mate byId = mateRepository.findById(mate).orElseThrow();

            MateComments mateComments = MateComments.builder()
                    .mateCommentsId(mateCommentsDTO.getCommentsId())
                    .comment(mateCommentsDTO.getComment())
                    .mate(byId)
                    .createdAt(mateCommentsDTO.getCreatedAt())
                    .user(user)
                    .mateCommentsLike(0)
                    .build();

            mateCommentsRepository.save(mateComments);
            notificationService.notifyComment(mate, "댕냥메이트");

            return createSuccessResponse("댓글이 등록되었습니다.", HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return createErrorResponse(MSG_USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return createErrorResponse("댓글 등록 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, String> modifyComment(String token, Long mateCommentsId, MateCommentsDTO mateCommentsDTO) {

        try {
            User user = userRepository.findByEmail(tokenProvider.getEmailBytoken(token))
                    .orElseThrow(() -> new EntityNotFoundException(MSG_USER_NOT_FOUND));

            MateComments mateComments = mateCommentsRepository.findById(mateCommentsId)
                    .orElseThrow(() -> new EntityNotFoundException(MSG_COMMENT_NOT_FOUND));

            checkOwnership(mateComments, user);
            modifyMateCommentsFields(mateComments, mateCommentsDTO);
            mateCommentsRepository.save(mateComments);

            return createSuccessResponse("댓글이 수정되었습니다.", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return createErrorResponse(MSG_COMMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return createErrorResponse(MSG_OWNER_ACCESS_DENIED, HttpStatus.FORBIDDEN);
        }
    }

    @Transactional
    public Map<String, String> deleteComment(String token, Long mateCommentsId) {
        try {
            User user = userRepository.findByEmail(tokenProvider.getEmailBytoken(token))
                    .orElseThrow(() -> new EntityNotFoundException(MSG_USER_NOT_FOUND));

            MateComments mateComments = mateCommentsRepository.findById(mateCommentsId)
                    .orElseThrow(() -> new EntityNotFoundException(MSG_COMMENT_NOT_FOUND));

            checkOwnership(mateComments, user);
            mateCommentsRepository.delete(mateComments);

            return createSuccessResponse("댓글이 삭제되었습니다.", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return createErrorResponse(MSG_COMMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return createErrorResponse(MSG_OWNER_ACCESS_DENIED, HttpStatus.FORBIDDEN);
        }
    }

    @Transactional
    public void setHeart(MateComments mateComments, User user, Boolean msg) {
        boolean hasUserLiked = mateCommentsLikeRepository.findByMateCommentsAndUser(mateComments, user).isPresent();

        if (msg) {
            // 좋아요 추가
            if (!hasUserLiked) {
                MateCommentsLike mateCommentsLike = new MateCommentsLike(mateComments, user);
                mateComments.getMateCommentsLikes().add(mateCommentsLike);
                mateComments.setMateCommentsLike(mateComments.getMateCommentsLike() + 1);
                mateCommentsRepository.save(mateComments);
                notifyCommentLike(mateComments);
            }
        } else {
            // 좋아요 취소
            if (hasUserLiked) {
                MateCommentsLike userLike = mateCommentsLikeRepository.findByMateCommentsAndUser(mateComments, user)
                        .orElseThrow(() -> new RuntimeException("사용자의 좋아요가 해당 댓글에 없습니다."));
                mateComments.getMateCommentsLikes().remove(userLike);
                mateCommentsLikeRepository.delete(userLike);
                mateComments.setMateCommentsLike(mateComments.getMateCommentsLike() - 1);
                mateCommentsRepository.save(mateComments);
            }
        }
    }
    //좋아요 클릭
    @Transactional
    public Map<String, String> clickLike(Long mateCommentId, String token){

        User user = userRepository.findByEmail(tokenProvider.getEmailBytoken(token))
                .orElseThrow(() -> new EntityNotFoundException(MSG_USER_NOT_FOUND));

        MateComments mateComments = mateCommentsRepository.findById(mateCommentId)
                .orElseThrow(() -> new EntityNotFoundException(MSG_COMMENT_NOT_FOUND));

        // 이미 좋아요를 눌렀는지 확인
        boolean hasUserLiked = mateCommentsLikeRepository.findByMateCommentsAndUser(mateComments, user).isPresent();

        if (!hasUserLiked) {
            // 좋아요 추가
            setHeart(mateComments, user, true);
            return createSuccessResponse(mateCommentId + "번 댓글에 좋아요가 추가되었습니다.", HttpStatus.OK);
        } else {
            // 좋아요 취소
            setHeart(mateComments, user, false);
            return createSuccessResponse(mateCommentId + "번 댓글에 좋아요가 취소되었습니다.", HttpStatus.OK);
        }
    }

    private void notifyCommentLike(MateComments mateComments) {
        notificationService.notifyCommentLike(mateComments.getMateCommentsId(), "댕냥메이트");
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

    private void checkOwnership(MateComments mateComments, User user) {
        if (!mateComments.getUser().equals(user)) {
            throw new AccessDeniedException(MSG_OWNER_ACCESS_DENIED);
        }
    }

    private void modifyMateCommentsFields(MateComments mateComments, MateCommentsDTO mateCommentsDTO) {
        mateComments.setComment(mateCommentsDTO.getComment());
    }

}