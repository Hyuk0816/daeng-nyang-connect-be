package com.git.backend.daeng_nyang_connect.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModifyUserDto {

    private String nickname;
    private String info;
    private String password;

    private String city;
    private String town;

    private Boolean experience;

    private String name;
    private String mobile;
    private char gender;
}
