package com.sparta.post03.service;

import com.sparta.post03.dto.request.CommentRequestDto;
import com.sparta.post03.dto.request.PostRequestDto;
import com.sparta.post03.dto.response.*;
import com.sparta.post03.entity.Comment;
import com.sparta.post03.entity.Member;
import com.sparta.post03.entity.Post;
import com.sparta.post03.jwt.provider.JwtProvider;
import com.sparta.post03.repository.CommentRepository;
import com.sparta.post03.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

	private final PostService postService;
	private final CommentRepository commentRepository;
	private final JwtProvider jwtProvider;

	private Member validateMember(HttpServletRequest request) {
		if (!jwtProvider.validateToken(request.getHeader("Refresh-Token"))) {
			return null;
		}
		return jwtProvider.getMemberFromAuthentication();
	}
	//댓글이 있는지 없는지
	@Transactional(readOnly = true)
	public Comment isPresentComment(Long id){
		Optional<Comment> optionalComment = commentRepository.findById(id);
		return optionalComment.orElse(null);
	}

	public ResponseDto<?> createComment(CommentRequestDto commentRequestDto, HttpServletRequest request) {
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
		if(commentRequestDto.getContent()==null){
			return ResponseDto.fail("CONTENT_EMPTY", "작성된 글이 없습니다.");
		}

		Post post = postService.isPresentPost(commentRequestDto.getPostId());
		if (null == post) {
			return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
		}
		if(commentRequestDto.getContent()==null){return ResponseDto.fail("CONTENT_EMPTY", "작성된 댓글이 없습니다.");
		}


		Comment comment = Comment.builder()
				.post(post)
				.content(commentRequestDto.getContent())
				.member(member)
				.build();
		commentRepository.save(comment);
		return ResponseDto.success(
				CommentResponseDto.builder()
						.id(comment.getId())
						.author(comment.getMember().getUsername())
						.content(comment.getContent())
						.createdAt(comment.getCreatedAt())
						.modifiedAt(comment.getModifiedAt())
						.build()
		);
	}


//	//댓글 조회
//	@Transactional(readOnly = true)
//	public ResponseDto<?> getAllComment() {
//		List<CommentAllResponseDto> commentAllList = new ArrayList<>();
//		List<Comment> commentList = commentRepository.findAllByOrderByModifiedAtDesc();
//		for(Comment comment: commentList){
//			commentAllList.add(
//					CommentAllResponseDto.builder()
//							.id(comment.getId())
//							.content(comment.getContent())
//							.author(comment.getMember().getUsername())
//							.createdAt(comment.getCreatedAt())
//							.modifiedAt(comment.getModifiedAt())
//							.build()
//			);
//		}
//		return ResponseDto.success(commentAllList);
//	}
	//댓글 상세 조회
	@Transactional(readOnly = true)
	public ResponseDto<?> getComment(Long postId) {
		Comment comment = isPresentComment(postId);
		if(null == comment){
			return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 ID 입니다.");
		}
		return ResponseDto.success(
				CommentResponseDto.builder()
						.id(comment.getId())
						.content(comment.getContent())
						.author(comment.getMember().getUsername())
						.createdAt(comment.getCreatedAt())
						.modifiedAt(comment.getModifiedAt())
						.build()
		);
	}

	//댓글 수정
	@Transactional
	public ResponseDto<?> updateComment(Long id, CommentRequestDto requestDto, HttpServletRequest request) {
		if(null == request.getHeader("Refresh-Token")){
			return ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
		}
		if(null == request.getHeader("Authorization")){
			return ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
		}
		Member member = validateMember(request);
		if(null == member){
			return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
		}
		Post post = postService.isPresentPost(requestDto.getPostId());
		if(null == post){
			return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 ID 입니다. ");
		}
		Comment comment = isPresentComment(id);
		if(null == comment){
			return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 ID 입니다.");
		}

//		if(comment.validateMember(member)){
//			return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
//		}

		comment.update(requestDto);
		commentRepository.save(comment);
		return ResponseDto.success(
				CommentResponseDto.builder()
						.id(comment.getId())
						.author(comment.getMember().getUsername())
						.content(comment.getContent())
						.createdAt(comment.getCreatedAt())
						.modifiedAt(comment.getModifiedAt())
						.build()
		);
	}
	//댓글 삭제
	@Transactional
	public ResponseDto<?> deleteComment(Long id, HttpServletRequest request) {
		if(null == request.getHeader("Refresh-Token")){
			return ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
		}
		if(null == request.getHeader("Authorization")){
			return ResponseDto.fail("MEMBER_NOT_FOUND", "로그인이 필요합니다.");
		}

		Member member = validateMember(request);
		if(null == member){
			return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
		}

		Comment comment = isPresentComment(id);
		if(null == comment){
			return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 ID입니다.");
		}

//		if(comment.validateMember(member)){
//			return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
//		}

		commentRepository.delete(comment);
		return ResponseDto.success("success");
	}


}