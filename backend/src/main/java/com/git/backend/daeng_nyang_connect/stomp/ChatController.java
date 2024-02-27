package com.git.backend.daeng_nyang_connect.stomp;

import com.git.backend.daeng_nyang_connect.config.jwt.TokenProvider;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import com.git.backend.daeng_nyang_connect.user.repository.UserRepository;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Cacheable
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    public Set<ChatRoomResponseDTO> myRoom(@RequestHeader("access_token") String token) {
//        내가 속한 채팅방 리스트 - user
//        그 채팅방의 유저 뽑기
        String email = tokenProvider.getEmailBytoken(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        List<ChatRoom> myRoomList= chatRoomUserRepository.findMyChatRoom(user.getUserId());
        Set<ChatRoomResponseDTO> chatRoomSet = new HashSet<>();

        for(ChatRoom chatRoom:myRoomList){
            Message latestMessage = messageRepository.findLatestMessage(chatRoom);
            ChatRoomResponseDTO chatRoomInfo = new ChatRoomResponseDTO(chatRoom, latestMessage);
            chatRoomSet.add(chatRoomInfo);
        }
        return chatRoomSet;
    }

    @GetMapping("/room/detail")
    public ChatRoomInfo myRoomDetail(@RequestHeader("access_token") String token,
                                     @RequestParam("chatRoomId") Long chatRoomId) {
        String email = tokenProvider.getEmailBytoken(token);
        User user = userRepository.findByEmail(email).orElseThrow(
                ()->new NoSuchElementException("없는 유저")
        );
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                ()->new NoSuchElementException("없는 채팅방")
        );

        return new ChatRoomInfo(chatRoom, messageRepository.findByChatRoom(chatRoom));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addRoom(@RequestHeader("access_token") String token,
                                     @RequestParam("animalId") Long animalId){
        ChatRoom newChatRoom = chatService.addChatRoom(token, animalId);
        return ResponseEntity.status(200).body(newChatRoom);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteRoom(@RequestHeader("access_token") String token,
                                        @RequestParam("roomId") Long roomId){
        chatService.deleteChatRoom(token, roomId);
        return ResponseEntity.status(200).body("채팅방 삭제 완료");
    }
}
