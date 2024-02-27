package com.git.backend.daeng_nyang_connect.stomp;

import com.git.backend.daeng_nyang_connect.animal.entity.Animal;
import com.git.backend.daeng_nyang_connect.animal.repository.AnimalRepository;
import com.git.backend.daeng_nyang_connect.config.jwt.TokenProvider;
import com.git.backend.daeng_nyang_connect.notify.service.NotificationService;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import com.git.backend.daeng_nyang_connect.user.repository.UserRepository;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Cacheable
public class ChatService {
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final TokenProvider tokenProvider;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final MessageRepository messageRepository;
    private final AnimalRepository animalRepository;
    private final NotificationService notificationService;

    public ChatRoom addChatRoom(String token, Long animalId) {
        String email = tokenProvider.getEmailBytoken(token);
        User sender = userRepository.findByEmail(email).orElseThrow(
                ()->new NoSuchElementException("토큰을 확인해주세요.")
        );
        Animal board = animalRepository.findById(animalId).orElseThrow(
                ()->new NoSuchElementException("없는 게시글입니다.")
        );

        if(board.getUser().equals(sender)){
            throw new IllegalArgumentException("당신의 게시글입니다.");
        }


        // 예시로 chatRoom을 생성하는 코드
        ChatRoom newChatRoom = ChatRoom.builder()
                .animal(board)
                .build();

        chatRoomRepository.save(newChatRoom);

        addUser(newChatRoom, sender);
        addUser(newChatRoom, board.getUser());

        return newChatRoom;
    }

    public void deleteChatRoom(String token, Long roomId) {
        String email = tokenProvider.getEmailBytoken(token);
        if(userRepository.findByEmail(email).isEmpty()) {
            throw new NoSuchElementException("토큰을 확인해주세요.");
        }

        if(chatRoomRepository.findById(roomId).isEmpty()){
            throw new NoSuchElementException("없는 방입니다.");
        }

        chatRoomRepository.deleteById(roomId);
    }

    public void addUser(ChatRoom chatRoom, User user){
        ChatRoomUser addUser = ChatRoomUser.builder().chatRoom(chatRoom).user(user).build();
        chatRoomUserRepository.save(addUser);
    }

    public MessageDTO handleChatMessage(MessageDTO message, String token) {
        String email = tokenProvider.getEmailBytoken(token);
        User sender = userRepository.findByEmail(email).orElseThrow(
                () -> new NoSuchElementException("없는 유저입니다")
        );

        ChatRoom chatRoom = chatRoomRepository.findById(message.getRoomId()).orElseThrow(
                () -> new NoSuchElementException("없는 채팅방입니다")
        );
//
//        if (!chatRoom.getUserList().contains(chatRoomUserRepository.findByUser(sender))) {
//            throw new NoSuchElementException("해당 채팅방에 당신은 없습니다");
//        }
        message.setCreatedAt(TimestampToFormattedString(nowDate()));
        notificationService.notifyNewChatMessage(message.getRoomId(), sender.getNickname());

        return message;
    }

    public void saveMessage(MessageDTO message){
        User sender = userRepository.findById(message.getSenderId()).orElseThrow(
                ()-> new NoSuchElementException("유저를 확인해주세요.")
        );
        ChatRoom chatRoom = chatRoomRepository.findById(message.getRoomId()).orElseThrow(
                ()->new NoSuchElementException("없는 채팅방입니다.")
        );
        Message newMessage = Message.builder()
                .sender(sender)
                .content(message.getContent())
                .chatRoom(chatRoom)
                .createdAt(nowDate())
                .build();

        messageRepository.save(newMessage);
        messageRepository.flush(); // 이 부분을 추가해보세요
    }

    public Timestamp nowDate(){
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        return Timestamp.valueOf(currentDateTime);
    }

    public String TimestampToFormattedString(Timestamp time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
        return dateFormat.format(time);
    }


}

