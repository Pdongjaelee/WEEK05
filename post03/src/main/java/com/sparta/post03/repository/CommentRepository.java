package com.sparta.post03.repository;

import com.sparta.post03.entity.Comment;
import com.sparta.post03.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByOrderByModifiedAtDesc();

}
