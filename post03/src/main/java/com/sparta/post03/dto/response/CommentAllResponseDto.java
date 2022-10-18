package com.sparta.post03.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentAllResponseDto {

    private Long id; //comment_id
    private String author; //username
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}
