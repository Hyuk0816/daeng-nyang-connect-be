package com.git.backend.daeng_nyang_connect.lost.comments.controller;

import com.git.backend.daeng_nyang_connect.lost.comments.dto.LostCommentsDTO;
import com.git.backend.daeng_nyang_connect.lost.comments.service.LostCommentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@RestController
@Cacheable
@Tag(name = "미아센터 댓글 API")
@RequestMapping("/api/lost/comments")
public class LostCommentsController {
    private final LostCommentsService lostCommentsService;

    //댓글 등록
    @Operation(summary = "댓글 작성")
    @PostMapping("/post")
    public Map<?,?>addComments(@RequestHeader("access_token") String token,
                               @RequestParam("id")Long lostBoardId,
                               @RequestBody LostCommentsDTO lostCommentsDTO){
        return lostCommentsService.lostCommentAdd(token, lostBoardId, lostCommentsDTO);
    }

    //댓글 수정
    @Operation(summary = "댓글 수정")
    @PutMapping("/modify")
    public Map<?,?> modifyComments(@RequestHeader("access_token") String token,
                                   @RequestParam("id")Long lostCommentsId,
                                   @RequestBody LostCommentsDTO lostCommentsDTO){
        return lostCommentsService.lostCommentModify(token, lostCommentsId, lostCommentsDTO);
    }

    //댓글 삭제
    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/delete")
    public Map<?,?> deleteComments(@RequestHeader("access_token")String token,
                                   @RequestParam("id")Long lostCommentsId){
        return lostCommentsService.lostCommentDelete(token, lostCommentsId);
    }
}
