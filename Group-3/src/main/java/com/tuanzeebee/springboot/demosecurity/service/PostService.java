package com.tuanzeebee.springboot.demosecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tuanzeebee.springboot.demosecurity.dao.PostDTO;
import com.tuanzeebee.springboot.demosecurity.entity.Post;
import com.tuanzeebee.springboot.demosecurity.entity.Recipe;
import com.tuanzeebee.springboot.demosecurity.entity.User;
import com.tuanzeebee.springboot.demosecurity.repository.PostRepository;
import com.tuanzeebee.springboot.demosecurity.repository.RecipeRepository;
import com.tuanzeebee.springboot.demosecurity.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashSet;

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
        try {
            return postRepository.findByRecipeIdOrderByCreatedAtDesc(recipeId).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy danh sách bài đăng: " + e.getMessage());
        }
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

    @Transactional
    public PostDTO likePost(Long postId, Long userId) {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Khong tim thay bai dang voi ID: " + postId));
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Khong tim thay nguoi dung voi ID: " + userId));
            
            // Kiểm tra xem người dùng đã like chưa
            if (post.getLikedByUsers().contains(user)) {
                throw new RuntimeException("Nguoi dung da thich bai dang nay");
            }
            
            post.getLikedByUsers().add(user);
            Post updatedPost = postRepository.save(post);
            return convertToDTO(updatedPost);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi thích bài đăng: " + e.getMessage());
        }
    }

    @Transactional
    public PostDTO unlikePost(Long postId, Long userId) {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Khong tim thay bai dang voi ID: " + postId));
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Khong tim thay nguoi dung voi ID: " + userId));
            
            // Kiểm tra xem người dùng đã like chưa
            if (!post.getLikedByUsers().contains(user)) {
                throw new RuntimeException("Nguoi dung chua thich bai dang nay");
            }
            
            post.getLikedByUsers().remove(user);
            Post updatedPost = postRepository.save(post);
            return convertToDTO(updatedPost);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi bỏ thích bài đăng: " + e.getMessage());
        }
    }

    private PostDTO convertToDTO(Post post) {
        try {
            PostDTO dto = new PostDTO();
            dto.setId(post.getId());
            
            // Chỉ set user nếu user tồn tại
            if (post.getUser() != null) {
                try {
                    dto.setUser(userService.getUserById(post.getUser().getId()));
                } catch (Exception e) {
                    // Nếu không lấy được thông tin user, set null
                    dto.setUser(null);
                }
            }
            
            // Chỉ set recipe nếu recipe tồn tại
            if (post.getRecipe() != null) {
                try {
                    dto.setRecipe(recipeService.getRecipeById(post.getRecipe().getId()));
                } catch (Exception e) {
                    // Nếu không lấy được thông tin recipe, set null
                    dto.setRecipe(null);
                }
            }
            
            dto.setContent(post.getContent());
            dto.setCreatedAt(post.getCreatedAt());
            
            // Set số lượng like và danh sách user đã like
            dto.setLikesCount(post.getLikedByUsers().size());
            try {
                dto.setLikedByUsers(post.getLikedByUsers().stream()
                    .map(user -> userService.getUserById(user.getId()))
                    .collect(Collectors.toSet()));
            } catch (Exception e) {
                // Nếu không lấy được thông tin user đã like, set empty set
                dto.setLikedByUsers(new HashSet<>());
            }
            
            // Set số lượng comment
            dto.setCommentsCount(post.getComments().size());
            
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi chuyển đổi dữ liệu: " + e.getMessage());
        }
    }

    public long countPosts() {
        return postRepository.count();
    }
}