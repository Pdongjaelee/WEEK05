package com.sparta.post03.controller;

import com.sparta.post03.dto.request.PostRequestDto;
import com.sparta.post03.dto.response.ResponseDto;
import com.sparta.post03.entity.Post;
import com.sparta.post03.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    //게시글 작성
    @PostMapping("/api/auth/posts")
    public ResponseDto<?> createPost(@RequestBody PostRequestDto postRequestDto, HttpServletRequest request){
        return postService.createPost(postRequestDto, request);
    }
    //게시글 조회
    @GetMapping("/api/posts")
    public ResponseDto<?> getAllPosts(){
        return postService.getAllPosts();
    }

    //게시글 아이디로 상세조회
    @GetMapping("/api/posts/{id}")
    public ResponseDto<?> getPost(@PathVariable Long id){
        return postService.getPost(id);
    }

    //게시글 수정
    @PutMapping("/api/auth/posts/{id}")
    public ResponseDto<?> updatePost(@PathVariable Long id,
                                     @RequestBody PostRequestDto postRequestDto,
                                     HttpServletRequest request)
    {
        return postService.updatePost(id, postRequestDto, request);
    }
    //게시글 삭제
    @DeleteMapping(value = "/api/auth/posts/{id}")
    public ResponseDto<?> deletePost(@PathVariable Long id, HttpServletRequest request){
        return postService.deletePost(id, request);
    }
}
