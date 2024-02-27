package com.git.backend.daeng_nyang_connect.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddExtraInfoDto {

    private String nickname;
    private String mobile;
    private char gender;
    private String city;
    private String town;
    private Boolean experience;

}
