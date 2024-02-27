package com.git.backend.daeng_nyang_connect.user.dto;

import com.git.backend.daeng_nyang_connect.user.role.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDto {

    private String email;
    private String password;
    private String rawPassword;

}
