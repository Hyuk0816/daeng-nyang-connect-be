package com.git.backend.daeng_nyang_connect.stomp;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomUserListDTO {
    private Long userId;
    private String nickname;
    private String userThumbnail;

    public ChatRoomUserListDTO(ChatRoomUser chatRoomUser) {
        this.nickname = chatRoomUser.getUser().getNickname();
        this.userThumbnail = chatRoomUser.getUser().getMyPage().getImg();
        this.userId = chatRoomUser.getUser().getUserId();
    }
}
