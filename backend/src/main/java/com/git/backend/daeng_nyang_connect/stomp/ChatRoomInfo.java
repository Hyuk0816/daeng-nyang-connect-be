package com.git.backend.daeng_nyang_connect.stomp;

import lombok.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomInfo {
    private Long chatRoomId;
    private List<ChatRoomUserListDTO> userList = new ArrayList<>();
    private String animalImage;
    private String animalName;
    private String animalAge;
    private String breed;
    private List<Message> messageList = new ArrayList<>();


    public ChatRoomInfo(ChatRoom chatRoom, List<Message> messageList) {
        this.chatRoomId = chatRoom.getChatRoomId();
        for(ChatRoomUser chatRoomUser:chatRoom.getUserList()) {
            ChatRoomUserListDTO userList = new ChatRoomUserListDTO(chatRoomUser);
            this.userList.add(userList);
        }
//        for (Message message : messageList){
//            message.setCreatedAt(TimestampToFormattedString(message.getCreatedAt()));
//        }
        this.messageList = messageList;
        this.animalAge = chatRoom.getAnimal().getAge();
        this.animalImage = chatRoom.getAnimal().getImages().get(0).getUrl();
        this.animalName = chatRoom.getAnimal().getAnimalName();
        this.breed = chatRoom.getAnimal().getBreed();
    }

    public String TimestampToFormattedString(Timestamp time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
        return dateFormat.format(time);
    }

}
