package com.tuanzeebee.springboot.demosecurity.controller;

import com.tuanzeebee.springboot.demosecurity.entity.User;
import com.tuanzeebee.springboot.demosecurity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/avatars/";

    @ModelAttribute
    public void addUserToModel(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            if (!username.equals("anonymousUser")) {
                User user = userService.findByUsername(username);
                if (user != null) {
                    model.addAttribute("user", user);
                }
            }
        }
    }

    @GetMapping
    public String showProfile(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        if (username == null || username.equals("anonymousUser")) {
            return "redirect:/login";
        }

        try {
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new RuntimeException("Không tìm thấy người dùng");
            }
            model.addAttribute("user", user);
            return "profile";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "error/500";
        }
    }

    @GetMapping("/edit")
    public String showEditProfile(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        if (username == null || username.equals("anonymousUser")) {
            return "redirect:/login";
        }

        try {
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new RuntimeException("Không tìm thấy người dùng");
            }
            model.addAttribute("user", user);
            return "edit-profile";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "error/500";
        }
    }

    @PostMapping("/edit")
    public String updateProfile(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("bio") String bio,
            @RequestParam(value = "currentPassword", required = false) String currentPassword,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            Authentication authentication,
            Model model) {

        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            if (user == null) {
                throw new RuntimeException("Không tìm thấy người dùng");
            }

            // Kiểm tra email đã tồn tại chưa (nếu email mới khác email cũ)
            if (!email.equals(user.getEmail())) {
                try {
                    userService.findByEmail(email);
                    throw new RuntimeException("Email này đã được sử dụng bởi tài khoản khác");
                } catch (RuntimeException e) {
                    // Nếu không tìm thấy email thì có thể sử dụng
                }
            }

            // Cập nhật thông tin cơ bản
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setBio(bio);

            // Xử lý thay đổi mật khẩu
            if (currentPassword != null && !currentPassword.isEmpty()) {
                if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                    throw new RuntimeException("Mật khẩu hiện tại không đúng");
                }

                if (newPassword == null || newPassword.isEmpty()) {
                    throw new RuntimeException("Vui lòng nhập mật khẩu mới");
                }

                if (!newPassword.equals(confirmPassword)) {
                    throw new RuntimeException("Mật khẩu mới không khớp");
                }

                if (newPassword.length() < 6) {
                    throw new RuntimeException("Mật khẩu mới phải có ít nhất 6 ký tự");
                }

                user.setPassword(passwordEncoder.encode(newPassword));
            }

            // Xử lý upload avatar
            if (avatar != null && !avatar.isEmpty()) {
                // Kiểm tra kích thước file (tối đa 5MB)
                if (avatar.getSize() > 5 * 1024 * 1024) {
                    throw new RuntimeException("Kích thước ảnh không được vượt quá 5MB");
                }

                // Kiểm tra định dạng file
                String contentType = avatar.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new RuntimeException("File không phải là ảnh");
                }

                // Tạo thư mục nếu chưa tồn tại
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Tạo tên file ngẫu nhiên
                String fileName = UUID.randomUUID().toString() + "_" + avatar.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);

                // Lưu file
                Files.copy(avatar.getInputStream(), filePath);

                // Cập nhật đường dẫn avatar trong database
                user.setAvatar("/uploads/avatars/" + fileName);
            }

            // Lưu thông tin người dùng
            userService.save(user);

            // Thêm thông báo thành công vào model
            model.addAttribute("success", "Cập nhật thông tin thành công!");
            model.addAttribute("user", user);
            
            // Hiển thị thông báo trong 3 giây trước khi chuyển hướng
            return "edit-profile";

        } catch (Exception e) {
            // Giữ lại thông tin đã nhập
            User user = userService.findByUsername(authentication.getName());
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setBio(bio);
            
            model.addAttribute("user", user);
            model.addAttribute("error", e.getMessage());
            return "edit-profile";
        }
    }
} 