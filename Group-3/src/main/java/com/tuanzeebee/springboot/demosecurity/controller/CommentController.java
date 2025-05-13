package com.tuanzeebee.springboot.demosecurity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tuanzeebee.springboot.demosecurity.dao.CommentDTO;
import com.tuanzeebee.springboot.demosecurity.entity.Comment;
import com.tuanzeebee.springboot.demosecurity.service.CommentService;

import java.util.List;
@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;
    
    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }
    
    @PostMapping("/user/{userId}/post/{postId}")
    public ResponseEntity<CommentDTO> createComment(@RequestBody Comment comment, 
                                                   @PathVariable Long userId, 
                                                   @PathVariable Long postId) {
        return new ResponseEntity<>(commentService.createComment(comment, userId, postId), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long id, @RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.updateComment(id, comment));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}