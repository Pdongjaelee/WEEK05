package com.sparta.post03.dto.response;

import com.sparta.post03.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
