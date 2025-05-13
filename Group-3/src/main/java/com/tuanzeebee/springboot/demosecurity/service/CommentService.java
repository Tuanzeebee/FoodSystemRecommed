package com.tuanzeebee.springboot.demosecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tuanzeebee.springboot.demosecurity.dao.CommentDTO;
import com.tuanzeebee.springboot.demosecurity.entity.Comment;
import com.tuanzeebee.springboot.demosecurity.entity.Post;
import com.tuanzeebee.springboot.demosecurity.entity.User;
import com.tuanzeebee.springboot.demosecurity.repository.CommentRepository;
import com.tuanzeebee.springboot.demosecurity.repository.PostRepository;
import com.tuanzeebee.springboot.demosecurity.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    @Autowired
    public CommentService(CommentRepository commentRepository, UserRepository userRepository, 
                         PostRepository postRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public CommentDTO getCommentById(Long id) {
        return commentRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }
public CommentDTO createComment(Comment comment, Long userId, Long postId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    
    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
    
    comment.setUser(user);
    comment.setPost(post);
    Comment savedComment = commentRepository.save(comment);
    return convertToDTO(savedComment);
}

        
            public CommentDTO updateComment(Long id, Comment updatedComment) {
                return commentRepository.findById(id)
                        .map(comment -> {
                            comment.setContent(updatedComment.getContent());
                            return convertToDTO(commentRepository.save(comment));
                        })
                        .orElseThrow(() -> new RuntimeException("Comment not found"));
            }
        
            public void deleteComment(Long id) {
                commentRepository.deleteById(id);
            }
        
            private CommentDTO convertToDTO(Comment comment) {
                CommentDTO dto = new CommentDTO();
                dto.setId(comment.getId());
                dto.setContent(comment.getContent());
                dto.setCreatedAt(comment.getCreatedAt());
                dto.setPostId(comment.getPost().getId());
                
                if (comment.getUser() != null) {
                    dto.setUser(userService.getUserById(comment.getUser().getId()));
                }
                
                return dto;
            }
        }
        