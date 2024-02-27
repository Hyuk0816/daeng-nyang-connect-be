package com.git.backend.daeng_nyang_connect.mypet.board.service;

import com.git.backend.daeng_nyang_connect.config.jwt.TokenProvider;
import com.git.backend.daeng_nyang_connect.mypet.board.dto.MyPetBoardLikeDTO;
import com.git.backend.daeng_nyang_connect.mypet.board.dto.MyPetDTO;
import com.git.backend.daeng_nyang_connect.mypet.board.dto.MyPetResponseDTO;
import com.git.backend.daeng_nyang_connect.mypet.board.entity.MyPet;
import com.git.backend.daeng_nyang_connect.mypet.board.entity.MyPetBoardLike;
import com.git.backend.daeng_nyang_connect.mypet.board.entity.MyPetImage;
import com.git.backend.daeng_nyang_connect.mypet.board.repository.MyPetBoardLikeRepository;
import com.git.backend.daeng_nyang_connect.mypet.board.repository.MyPetRepository;
import com.git.backend.daeng_nyang_connect.mypet.comments.dto.MyPetCommentsLikeDTO;
import com.git.backend.daeng_nyang_connect.mypet.comments.dto.MyPetCommentsResponseDTO;
import com.git.backend.daeng_nyang_connect.mypet.comments.entity.MyPetComments;
import com.git.backend.daeng_nyang_connect.mypet.comments.entity.MyPetCommentsLike;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Cacheable
public class MyPetService {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final MyPetRepository myPetRepository;
    private final MyPetImgUpload myPetImgUpload;
    private final MyPetBoardLikeRepository myPetBoardLikeRepository;
    private final NotificationService notificationService;

    private static final String MSG_USER_NOT_FOUND = "유저를 찾을 수 없습니다.";
    private static final String MSG_BOARD_NOT_FOUND = "게시물을 찾을 수 없습니다.";
    private static final String MSG_OWNER_ACCESS_DENIED = "게시물의 소유자가 아닙니다.";

    public List<MyPetResponseDTO> findAllMyPet() {
        List<MyPet> myPetList = myPetRepository.findAll();
        return myPetList.stream()
                .map(this::convertToMyPetResponseDTO)
                .collect(Collectors.toList());
    }

    public MyPetResponseDTO getThisBoard(Long myPet) {

        MyPet thisBoard = myPetRepository.findById(myPet)
                .orElseThrow(() -> new NoSuchElementException(MSG_BOARD_NOT_FOUND));

        return convertToMyPetResponseDTO(thisBoard);
    }

    private MyPetResponseDTO convertToMyPetResponseDTO(MyPet myPet) {

        List<String> imgUrls = myPet.getImg().stream()
                .map(MyPetImage::getUrl)
                .collect(Collectors.toList());

        List<MyPetCommentsResponseDTO> comments = myPet.getComment().stream()
                .map(this::convertToMyPetCommentsResponseDTO)
                .collect(Collectors.toList());

        List<MyPetBoardLikeDTO> likes = myPet.getMyPetLikes().stream()
                .map(like -> MyPetBoardLikeDTO.builder()
                        .likeId(like.getMyPetBoardLikeId())
                        .userId(like.getUser().getUserId())
                        .build())
                .collect(Collectors.toList());

        String userThumbnail = null;
        if (myPet.getUser() != null && myPet.getUser().getMyPage() != null) {
            userThumbnail = myPet.getUser().getMyPage().getImg();
        }

        return MyPetResponseDTO.builder()
                .boardId(myPet.getMyPetBoardId())
                .userId(myPet.getUser().getUserId())
                .nickname(myPet.getUser().getNickname())
                .userThumbnail(userThumbnail)
                .kind(myPet.getKind())
                .text(myPet.getText())
                .img(imgUrls)
                .createdAt(myPet.getCreatedAt())
                .comments(comments)
                .likes(likes)
                .build();
    }
    private MyPetCommentsResponseDTO convertToMyPetCommentsResponseDTO(MyPetComments comments) {
        String userThumbnail = null;
        if (comments.getUser() != null && comments.getUser().getMyPage() != null) {
            userThumbnail = comments.getUser().getMyPage().getImg();
        }

        return MyPetCommentsResponseDTO.builder()
                .commentsId(comments.getMyPetCommentsId())
                .userId(comments.getUser().getUserId())
                .nickname(comments.getUser().getNickname())
                .userThumbnail(userThumbnail)
                .comment(comments.getComment())
                .createdAt(comments.getCreatedAt())
                .likes(convertToMyPetCommentsLikeDTOList(comments.getMyPetCommentsLikes()))
                .build();
    }

    private List<MyPetCommentsLikeDTO> convertToMyPetCommentsLikeDTOList(List<MyPetCommentsLike> myPetCommentsLikes) {
        return myPetCommentsLikes.stream()
                .map(like -> MyPetCommentsLikeDTO.builder()
                        .likeId(like.getMyPetCommentsLikeId())
                        .userId(like.getUser().getUserId())
                        .build())
                .collect(Collectors.toList());
    }
    public List<MyPetResponseDTO> searchBoard(String keyword) {
        List<MyPet> myPetList = myPetRepository.findByTextContaining(keyword);
        return myPetList.stream()
                .map(this::convertToMyPetResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, String> postMyPet(MyPetDTO myPetDTO, String token, List<MultipartFile> files) {
        try {
            User user = userRepository.findByEmail(tokenProvider.getEmailBytoken(token))
                    .orElseThrow(() -> new NoSuchElementException(MSG_USER_NOT_FOUND));

            MyPet myPet = MyPet.builder()
                    .myPetBoardId(myPetDTO.getBoardId())
                    .user(user)
                    .kind(myPetDTO.getKind())
                    .text(myPetDTO.getText())
                    .createdAt(myPetDTO.getCreatedAt())
                    .myPetLike(0)
                    .build();

            myPetRepository.save(myPet);

            if (files != null && !files.isEmpty()) {
                myPetImgUpload.uploadMyPetImgs(myPet, myPetDTO.getText(), files);
            }

            return createSuccessResponse("게시물이 등록되었습니다.", HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return createErrorResponse(MSG_USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return createErrorResponse("게시물 등록 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public Map<String, String> modifyMyPet(Long myPetId, MyPetDTO myPetDTO, String token) {
        try {
            User user = userRepository.findByEmail(tokenProvider.getEmailBytoken(token))
                    .orElseThrow(() -> new EntityNotFoundException(MSG_USER_NOT_FOUND));

            MyPet myPet = myPetRepository.findById(myPetId)
                    .orElseThrow(() -> new EntityNotFoundException(MSG_BOARD_NOT_FOUND));

            checkOwnership(myPet, user);
            modifyMyPetFields(myPet, myPetDTO);
            myPetRepository.save(myPet);

            return createSuccessResponse("게시물이 수정되었습니다.", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return createErrorResponse(MSG_BOARD_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return createErrorResponse(MSG_OWNER_ACCESS_DENIED, HttpStatus.FORBIDDEN);
        }
    }

    @Transactional
    public Map<String, String> deleteMyPet(Long myPetId, String token) {
        try {
            User user = userRepository.findByEmail(tokenProvider.getEmailBytoken(token))
                    .orElseThrow(() -> new EntityNotFoundException(MSG_USER_NOT_FOUND));

            MyPet myPet = myPetRepository.findById(myPetId)
                    .orElseThrow(() -> new EntityNotFoundException(MSG_BOARD_NOT_FOUND));

            checkOwnership(myPet, user);
            myPetRepository.delete(myPet);

            return createSuccessResponse("게시물이 삭제되었습니다.", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return createErrorResponse(MSG_BOARD_NOT_FOUND, HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return createErrorResponse(MSG_OWNER_ACCESS_DENIED, HttpStatus.FORBIDDEN);
        }
    }

    @Transactional
    public void setHeart(MyPet myPet, User user, Boolean msg) {
        boolean hasUserLiked = myPetBoardLikeRepository.findByMyPetAndUser(myPet, user).isPresent();

        if (msg) {
            // 좋아요 추가
            if (!hasUserLiked) {
                MyPetBoardLike myPetBoardLike = new MyPetBoardLike(myPet, user);
                myPet.getMyPetLikes().add(myPetBoardLike);
                myPet.setMyPetLike(myPet.getMyPetLike() + 1);
                myPetRepository.save(myPet);
                notifyPostLike(myPet);
            }
        } else {
            // 좋아요 취소
            if (hasUserLiked) {
                MyPetBoardLike userLike = myPetBoardLikeRepository.findByMyPetAndUser(myPet, user)
                        .orElseThrow(() -> new RuntimeException("사용자의 좋아요가 해당 게시글에 없습니다."));
                myPet.getMyPetLikes().remove(userLike);
                myPetBoardLikeRepository.delete(userLike);
                myPet.setMyPetLike(myPet.getMyPetLike() - 1);
                myPetRepository.save(myPet);
            }
        }
    }

    @Transactional
    public Map<String, String> clickLike(Long myPetId, String token) {
        User user = userRepository.findByEmail(tokenProvider.getEmailBytoken(token))
                .orElseThrow(() -> new EntityNotFoundException(MSG_USER_NOT_FOUND));

        MyPet myPet = myPetRepository.findById(myPetId)
                .orElseThrow(() -> new EntityNotFoundException(MSG_BOARD_NOT_FOUND));

        // 이미 좋아요를 눌렀는지 확인
        boolean hasUserLiked = myPetBoardLikeRepository.findByMyPetAndUser(myPet, user).isPresent();

        if (!hasUserLiked) {
            // 좋아요 추가
            setHeart(myPet, user, true);
            return createSuccessResponse(myPetId + "번 게시글에 좋아요가 추가되었습니다.", HttpStatus.OK);
        } else {
            // 좋아요 취소
            setHeart(myPet, user, false);
            return createSuccessResponse(myPetId + "번 게시글에 좋아요가 취소되었습니다.", HttpStatus.OK);
        }
    }

    private void notifyPostLike(MyPet myPet) {
        notificationService.notifyPostLike(myPet.getMyPetBoardId(), "나의 댕냥이");
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

    private void checkOwnership(MyPet myPet, User user) {
        if (!myPet.getUser().equals(user)) {
            throw new AccessDeniedException(MSG_OWNER_ACCESS_DENIED);
        }
    }

    private void modifyMyPetFields(MyPet myPet, MyPetDTO myPetDTO) {
        myPet.setKind(myPetDTO.getKind());
        myPet.setText(myPetDTO.getText());
    }

    public Map<String, Integer> getSize(){
        Map<String, Integer> response = new HashMap<>();
        Integer size = myPetRepository.findAll().size();
        response.put("size", size);
        return response;
    }

}
