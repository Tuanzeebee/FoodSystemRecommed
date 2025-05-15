package com.tuanzeebee.springboot.demosecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tuanzeebee.springboot.demosecurity.dao.PostDTO;
import com.tuanzeebee.springboot.demosecurity.entity.Post;
import com.tuanzeebee.springboot.demosecurity.entity.Recipe;
import com.tuanzeebee.springboot.demosecurity.entity.User;
import com.tuanzeebee.springboot.demosecurity.repository.PostRepository;
import com.tuanzeebee.springboot.demosecurity.repository.RecipeRepository;
import com.tuanzeebee.springboot.demosecurity.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final UserService userService;
    private final RecipeService recipeService;
    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, 
                      RecipeRepository recipeRepository, UserService userService, RecipeService recipeService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.userService = userService;
        this.recipeService = recipeService;
    }
    public List<PostDTO> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public PostDTO getPostById(Long id) {
        return postRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }
    public List<PostDTO> getPostsByUserId(Long userId) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public List<PostDTO> getPostsByRecipeId(Long recipeId) {
        return postRepository.findByRecipeIdOrderByCreatedAtDesc(recipeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public PostDTO createPost(Post post, Long userId, Long recipeId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            Recipe recipe = null;
            if (recipeId != null) {
                recipe = recipeRepository.findById(recipeId)
                        .orElseThrow(() -> new RuntimeException("Recipe not found with ID: " + recipeId));
            }
            
            post.setUser(user);
            post.setRecipe(recipe);
            Post savedPost = postRepository.save(post);
            return convertToDTO(savedPost);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating post: " + e.getMessage());
        }
    }
    public PostDTO updatePost(Long id, Post updatedPost) {
        return postRepository.findById(id)
                .map(post -> {
                    post.setContent(updatedPost.getContent());
                    return convertToDTO(postRepository.save(post));
                })
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
    public PostDTO likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        post.getLikedByUsers().add(user);
        Post updatedPost = postRepository.save(post);
        return convertToDTO(updatedPost);
    }
    public PostDTO unlikePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        post.getLikedByUsers().remove(user);
        Post updatedPost = postRepository.save(post);
        return convertToDTO(updatedPost);
    }
    private PostDTO convertToDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        
        if (post.getUser() != null) {
            dto.setUser(userService.getUserById(post.getUser().getId()));
        }
        
        if (post.getRecipe() != null) {
            dto.setRecipe(recipeService.getRecipeById(post.getRecipe().getId()));
        }
        
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setLikesCount(post.getLikedByUsers().size());
        dto.setCommentsCount(post.getComments().size());
        
        return dto;
    }
}