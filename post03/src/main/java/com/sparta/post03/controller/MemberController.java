package com.sparta.post03.controller;


import com.sparta.post03.dto.request.LoginRequestDto;
import com.sparta.post03.dto.request.MemberRequestDto;
import com.sparta.post03.dto.response.MemberResponseDto;
import com.sparta.post03.dto.response.ResponseDto;
import com.sparta.post03.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    //회원가입
    @PostMapping("/api/member/signup")
    public ResponseDto<?> signup(@RequestBody @Valid MemberRequestDto memberRequestDto){
        return memberService.registerUser(memberRequestDto);
    }

    //로그인
    @PostMapping("/api/member/login")
    public ResponseDto<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse){
        return memberService.login(loginRequestDto, httpServletResponse);

    }
}
