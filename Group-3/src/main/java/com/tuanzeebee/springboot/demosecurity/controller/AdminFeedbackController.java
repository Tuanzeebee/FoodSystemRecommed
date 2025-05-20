package com.tuanzeebee.springboot.demosecurity.controller;

import com.tuanzeebee.springboot.demosecurity.dao.PostDTO;
import com.tuanzeebee.springboot.demosecurity.entity.Post;
import com.tuanzeebee.springboot.demosecurity.service.PostService;
import com.tuanzeebee.springboot.demosecurity.service.UserService;
import com.tuanzeebee.springboot.demosecurity.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/feedback")
public class AdminFeedbackController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private RecipeService recipeService;

    @GetMapping
    public String showFeedbackPage(Model model) {
        try {
            List<PostDTO> posts = postService.getAllPosts();
            model.addAttribute("posts", posts);
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("recipes", recipeService.getAllRecipes());
        } catch (Exception e) {
            model.addAttribute("message", 
                new Message("danger", "Lỗi khi tải danh sách bình luận: " + e.getMessage()));
        }
        return "admin/feedbackmanage";
    }

    @PostMapping("/add")
    public String addFeedback(@RequestParam Long userId,
                            @RequestParam Long recipeId,
                            @RequestParam String content,
                            RedirectAttributes redirectAttributes) {
        try {
            Post post = new Post();
            post.setContent(content);
            postService.createPost(post, userId, recipeId);
            redirectAttributes.addFlashAttribute("message", 
                new Message("success", "Thêm bình luận thành công!"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", 
                new Message("danger", "Lỗi khi thêm bình luận: " + e.getMessage()));
        }
        return "redirect:/admin/feedback";
    }

    @PostMapping("/update/{id}")
    public String updateFeedback(@PathVariable Long id, 
                               @RequestParam String content,
                               RedirectAttributes redirectAttributes) {
        try {
            Post post = new Post();
            post.setId(id);
            post.setContent(content);
            postService.updatePost(id, post);
            redirectAttributes.addFlashAttribute("message", 
                new Message("success", "Cập nhật bình luận thành công!"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", 
                new Message("danger", "Lỗi khi cập nhật bình luận: " + e.getMessage()));
        }
        return "redirect:/admin/feedback";
    }

    @PostMapping("/delete/{id}")
    public String deleteFeedback(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        try {
            postService.deletePost(id);
            redirectAttributes.addFlashAttribute("message", 
                new Message("success", "Xóa bình luận thành công!"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", 
                new Message("danger", "Lỗi khi xóa bình luận: " + e.getMessage()));
        }
        return "redirect:/admin/feedback";
    }

    // Helper class for flash messages
    private static class Message {
        private final String type;
        private final String content;

        public Message(String type, String content) {
            this.type = type;
            this.content = content;
        }

        public String getType() {
            return type;
        }

        public String getContent() {
            return content;
        }
    }
} 