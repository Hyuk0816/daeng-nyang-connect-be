//package com.git.backend.daeng_nyang_connect.stomp;
//
//import com.git.backend.daeng_nyang_connect.config.jwt.TokenProvider;
//import com.git.backend.daeng_nyang_connect.user.repository.UserRepository;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CustomChannelInterceptor implements ChannelInterceptor {
//    private final UserRepository userRepository;
//    private final ChatRoomRepository chatRoomRepository;
//    private final SimpMessagingTemplate messagingTemplate;
//    private final MessageRepository messageRepository;
//    private final TokenProvider tokenProvider;
//
//    public CustomChannelInterceptor(UserRepository userRepository, ChatRoomRepository chatRoomRepository,
//                                    SimpMessagingTemplate messagingTemplate, MessageRepository messageRepository,
//                                    TokenProvider tokenProvider) {
//        this.userRepository = userRepository;
//        this.chatRoomRepository = chatRoomRepository;
//        this.messagingTemplate = messagingTemplate;
//        this.messageRepository = messageRepository;
//        this.tokenProvider = tokenProvider;
//    }
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        // 메시지를 전송하기 전에 수행되는 로직
//        // ChatService의 handleChatMessage 로직을 여기에 통합
//
//        // 예시: 메시지의 payload를 확인하여 ChatMessageDTO로 변환 후 처리
//        Object payload = message.getPayload();
//        if (payload instanceof ChatMessageDTO) {
//            ChatMessageDTO chatMessageDTO = (ChatMessageDTO) payload;
//            handleChatMessage(chatMessageDTO);
//        }
//
//        return message;
//    }
//
//    private void handleChatMessage(ChatMessageDTO message) {
//        // ChatService의 handleChatMessage 로직을 가져와서 적용
//        String token = message.getToken();
//        String email = tokenProvider.getEmailBytoken(token);
//        User sender = userRepository.findByEmail(email).orElseThrow();
//        ChatRoom chatRoom = chatRoomRepository.findById(message.getRoomId()).orElseThrow();
//
//        if (!chatRoom.getUsers().contains(sender)) {
//            throw new NoSuchElementException("x");
//        }
//
//        Message sendMessage = Message.builder()
//                .content(message.getContent())
//                .chatRoom(chatRoom)
//                .sender(sender)
//                .build();
//
//        // 메시지 저장
//        messageRepository.save(sendMessage);
//
//        // 채팅방 내 모든 사용자에게 메시지 전송
//        messagingTemplate.convertAndSend("/topic/messages/" + chatRoom.getRoomName(), message);
//    }
//
//    @Override
//    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
//        // 메시지가 전송된 후에 수행되는 로직
//        // 필요한 경우 ChatService의 다른 메소드들을 여기에 통합
//    }
//
//    // 다른 필요한 메서드들을 추가로 구현할 수 있습니다.
//}
