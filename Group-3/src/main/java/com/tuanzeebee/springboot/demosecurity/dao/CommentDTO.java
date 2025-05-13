package com.tuanzeebee.springboot.demosecurity.dao;

import lombok.Data;
import java.time.LocalDateTime;
@Data
public class CommentDTO {
    private Long id;
    private UserDTO user;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;
}