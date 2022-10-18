package com.sparta.post03.service;

import com.sparta.post03.dto.request.PostRequestDto;
import com.sparta.post03.dto.response.PostAllResponseDto;
import com.sparta.post03.repository.PostRepository;
import com.sparta.post03.dto.response.PostResponseDto;
import com.sparta.post03.dto.response.ResponseDto;
import com.sparta.post03.entity.Member;
import com.sparta.post03.entity.Post;
import com.sparta.post03.jwt.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final JwtProvider jwtProvider;

    //사용자 유효성 검사
    public Member validateMember(HttpServletRequest request){
        if(!jwtProvider.validateToken(request.getHeader("Refresh-Token"))){
            return null;
        }
        return jwtProvider.getMemberFromAuthentication();
    }

    //게시판이 있는지 없는지
    public Post isPresentPost(Long id){
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }

    //url받아오기
    public String getImageUrlByPost(Post post){

        return post.getImageUrl();
    }

    public ResponseDto<?> createPost(PostRequestDto postRequestDto, HttpServletRequest request) {
        if(null == request.getHeader("Refresh-Token")){
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        if(null == request.getHeader("Authorization")){
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        Member member = validateMember(request);
        if(null == member){
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }
        if(postRequestDto.getTitle()==null){
            return ResponseDto.fail("TITLE_EMPTY", "제목 칸이 비어있습니다.");
        }
        if(postRequestDto.getContent()==null){
            return ResponseDto.fail("CONTENT_EMPTY", "작성된 글이 없습니다.");
        }
        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .imageUrl(postRequestDto.getImageUrl())
                .member(member)
                .build();
        postRepository.save(post);
        return ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .author(post.getMember().getUsername())
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .imageUrl(post.getImageUrl())
                        .build()
        );
    }
    //게시글 전체 조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPosts() {
        List<PostAllResponseDto> postAllList = new ArrayList<>();
        List<Post> postList = postRepository.findAllByOrderByModifiedAtDesc();
        for(Post post: postList){
            String url = getImageUrlByPost(post);
            postAllList.add(
                    PostAllResponseDto.builder()
                            .id(post.getId())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .author(post.getMember().getUsername())
                            .imageUrl(url)
                            .createdAt(post.getCreatedAt())
                            .modifiedAt(post.getModifiedAt())
                            .build()
            );
        }
        return ResponseDto.success(postAllList);
    }

    //게시글 아이디로 조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getPost(Long id) {
        Post post = isPresentPost(id);
        if(null == post){
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 ID 입니다.");
        }
        String url = getImageUrlByPost(post);
        return ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .author(post.getMember().getUsername())
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .imageUrl(url)
                        .build()
        );
    }

    //게시글 수정
    @Transactional
    public ResponseDto<?> updatePost(Long id, PostRequestDto postRequestDto, HttpServletRequest request) {
        if(null == request.getHeader("Refresh-Token")){
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if(null == request.getHeader("Authorization")){
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if(null == member){
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Post post = isPresentPost(id);
        if(null == post){
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        if(!post.validateMember(member)){
          return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
       }
        post.update(postRequestDto);
        postRepository.save(post);
        return ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .author(post.getMember().getUsername())
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .imageUrl(post.getImageUrl())
                        .build()
        );
    }

    //게시글 삭제
    @Transactional
    public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }
        Post post = isPresentPost(id);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }
        if (!post.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
       }
        postRepository.delete(post);
        return ResponseDto.success("delete success");
    }
}
