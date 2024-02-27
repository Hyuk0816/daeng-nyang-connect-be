package com.git.backend.daeng_nyang_connect.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindDto {

    private String name;
    private String mobile;
    private String email;

    private String newPassword;

}
