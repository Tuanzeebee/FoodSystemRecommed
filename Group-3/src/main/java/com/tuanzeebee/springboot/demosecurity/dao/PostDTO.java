package com.tuanzeebee.springboot.demosecurity.dao;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class PostDTO {
    private Long id;
    private UserDTO user;
    private RecipeDTO recipe;
    private String content;
    private LocalDateTime createdAt;
    private int likesCount;
    private int commentsCount;
    private Set<UserDTO> likedByUsers;
}