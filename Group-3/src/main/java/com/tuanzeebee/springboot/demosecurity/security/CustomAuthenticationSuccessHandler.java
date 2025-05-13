package com.tuanzeebee.springboot.demosecurity.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                       Authentication authentication) throws IOException, ServletException {
        
        HttpSession session = request.getSession();
        
        // Lưu thông tin người dùng đã đăng nhập vào session
        session.setAttribute("user", authentication.getName());
        
        // Chuyển hướng tùy thuộc vào vai trò
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isManager = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
        
        if (isAdmin) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else if (isManager) {
            response.sendRedirect(request.getContextPath() + "/manager/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}
