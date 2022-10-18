package com.sparta.post03.entity;

import com.sparta.post03.dto.request.CommentRequestDto;
import com.sparta.post03.dto.request.PostRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = LAZY)
    private Member member;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = LAZY)
    private Post post;


    public boolean validateMember(Member member){

        return !this.member.equals(member);
    }

    public void update(CommentRequestDto commentRequestDto){
        this.content = commentRequestDto.getContent();
    }


}