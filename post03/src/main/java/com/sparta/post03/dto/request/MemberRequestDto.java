package com.sparta.post03.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {

    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9]{4,12}", message = "닉네임양식을 확인해주세요!")
    private String username;

    @NotBlank
    @Pattern(regexp = "[a-z0-9]{4,32}", message = "비밀번호양식을 확인해주세요!")
    private String password;

    @NotBlank
    private String passwordConfirm;
}
