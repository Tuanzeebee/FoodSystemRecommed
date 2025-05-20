package com.tuanzeebee.springboot.demosecurity.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.tuanzeebee.springboot.demosecurity.entity.Post;

import java.time.LocalDateTime;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Post> findByRecipeIdOrderByCreatedAtDesc(Long recipeId);
    List<Post> findAllByOrderByCreatedAtDesc();
    
}