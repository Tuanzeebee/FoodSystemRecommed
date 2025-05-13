package com.tuanzeebee.springboot.demosecurity.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tuanzeebee.springboot.demosecurity.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Post> findByRecipeIdOrderByCreatedAtDesc(Long recipeId);
}