package com.tuanzeebee.springboot.demosecurity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tuanzeebee.springboot.demosecurity.dao.RecipeDTO;
import com.tuanzeebee.springboot.demosecurity.service.RecipeService;

@Controller
public class RecipeViewController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping("/recipe/view/{id}")
    public String viewRecipeDetail(@PathVariable Long id, Model model) {
        try {
            RecipeDTO recipeDTO = recipeService.getRecipeById(id);
            
            if (recipeDTO == null) {
                return "error/404";
            }
            
            model.addAttribute("recipe", recipeDTO);
            return "recipedetail";
        } catch (Exception e) {
            return "error/500";
        }
    }
}