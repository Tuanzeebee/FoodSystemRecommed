package com.tuanzeebee.springboot.demosecurity.controller;

import java.util.Optional;

import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.web.csrf.CsrfToken;

import com.tuanzeebee.springboot.demosecurity.entity.User;

@Controller
public class FeedbackController {

    @GetMapping("/feedback")
    public String showBlogPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String username = userDetails.getUsername();
            model.addAttribute("username", username);
        }
        return "feedback";
    }

}
