package com.tuanzeebee.springboot.demosecurity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tuanzeebee.springboot.demosecurity.dao.PostDTO;
import com.tuanzeebee.springboot.demosecurity.entity.Post;
import com.tuanzeebee.springboot.demosecurity.service.PostService;

import java.util.List;
@RestController
@RequestMapping("/api/posts")
public class PostControllerAPI {
    private final PostService postService;
    
    @Autowired
    public PostControllerAPI(PostService postService) {
        this.postService = postService;
    }
    
    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDTO>> getPostsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getPostsByUserId(userId));
    }
    
    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<PostDTO>> getPostsByRecipeId(@PathVariable Long recipeId) {
        return ResponseEntity.ok(postService.getPostsByRecipeId(recipeId));
    }
    
    @PostMapping("/user/{userId}/recipe/{recipeId}")
    public ResponseEntity<?> createPost(@RequestBody Post post, 
                                      @PathVariable Long userId, 
                                      @PathVariable Long recipeId) {
        try {
            PostDTO newPost = postService.createPost(post, userId, recipeId);
            return new ResponseEntity<>(newPost, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Không thể tạo bài đăng: " + e.getMessage());
        }
    }
    @PostMapping("/user/{userId}")
    public ResponseEntity<PostDTO> createPostWithoutRecipe(@RequestBody Post post, 
                                                          @PathVariable Long userId) {
        return new ResponseEntity<>(postService.createPost(post, userId, null), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id, @RequestBody Post post) {
        return ResponseEntity.ok(postService.updatePost(id, post));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{postId}/like/{userId}")
    public ResponseEntity<PostDTO> likePost(@PathVariable Long postId, @PathVariable Long userId) {
        return ResponseEntity.ok(postService.likePost(postId, userId));
    }
    
    @DeleteMapping("/{postId}/unlike/{userId}")
    public ResponseEntity<PostDTO> unlikePost(@PathVariable Long postId, @PathVariable Long userId) {
        return ResponseEntity.ok(postService.unlikePost(postId, userId));
    }
}