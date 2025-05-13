package com.tuanzeebee.springboot.demosecurity.controller;

import com.tuanzeebee.springboot.demosecurity.entity.Role;
import com.tuanzeebee.springboot.demosecurity.entity.User;
import com.tuanzeebee.springboot.demosecurity.repository.RoleRepository;
import com.tuanzeebee.springboot.demosecurity.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.HashSet;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @Autowired
    public RegisterController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping
    public String processRegistration(@Valid @ModelAttribute("user") User user, 
                                     BindingResult result, 
                                     Model model) {
        // Kiểm tra lỗi validation
        if (result.hasErrors()) {
            return "register";
        }

        try {
            // Tìm hoặc tạo role USER
            Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    // Nếu không tìm thấy role USER, tạo mới
                    Role newRole = new Role();
                    newRole.setName("USER");
                    return roleRepository.save(newRole);
                });
            
            // Gán role USER cho người dùng mới
            user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
            
            // Lưu user mới
            userService.createUser(user);
            
            // Thêm thông báo thành công
            model.addAttribute("registrationSuccess", true);
            return "login";
        } catch (Exception e) {
            model.addAttribute("registrationError", e.getMessage());
            return "register";
        }
    }
}
