package com.epi.epilog.app.dto;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor(access =  AccessLevel.PRIVATE)
public class CustomUserInfoDto {
    private Long memberId;
    private String loginId;
    private String password;
    private String name;
    private String code;
}
