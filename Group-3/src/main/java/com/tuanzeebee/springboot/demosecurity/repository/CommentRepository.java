package com.tuanzeebee.springboot.demosecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tuanzeebee.springboot.demosecurity.entity.Comment;

import java.util.List;
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);
}