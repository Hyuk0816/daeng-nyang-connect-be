package com.git.backend.daeng_nyang_connect.stomp;

import com.git.backend.daeng_nyang_connect.animal.entity.Animal;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponseDTO {

    private Long chatRoomId;
    private List<ChatRoomUserListDTO> userList = new ArrayList<>();
    private String latestContent;


    public ChatRoomResponseDTO(ChatRoom chatRoom, Message message) {
        this.chatRoomId = chatRoom.getChatRoomId();

        for(ChatRoomUser chatRoomUser:chatRoom.getUserList()) {
            ChatRoomUserListDTO userList = new ChatRoomUserListDTO(chatRoomUser);
            this.userList.add(userList);
        }
        if(message!=null){
            this.latestContent = message.getContent();
        }
    }
}
