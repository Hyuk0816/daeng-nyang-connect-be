package com.git.backend.daeng_nyang_connect.stomp;

import com.git.backend.daeng_nyang_connect.animal.entity.Animal;
import com.git.backend.daeng_nyang_connect.animal.entity.Kind;
import com.git.backend.daeng_nyang_connect.user.entity.User;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private Long roomId;
    private String content;
    private Long senderId;
    private String createdAt;
}
