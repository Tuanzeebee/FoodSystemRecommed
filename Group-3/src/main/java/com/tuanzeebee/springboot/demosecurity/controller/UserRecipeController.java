package com.tuanzeebee.springboot.demosecurity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tuanzeebee.springboot.demosecurity.dao.RecipeDTO;
import com.tuanzeebee.springboot.demosecurity.service.IngredientService;
import com.tuanzeebee.springboot.demosecurity.service.RecipeService;

import java.util.List;

@Controller
@RequestMapping("/recipes")
public class UserRecipeController {
    
    private final RecipeService recipeService;
    private final IngredientService ingredientService;
    
    @Autowired
    public UserRecipeController(RecipeService recipeService, IngredientService ingredientService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
    }
    
    @GetMapping
    public String getAllRecipes(Model model) {
        List<RecipeDTO> recipes = recipeService.getAllRecipes();
        model.addAttribute("recipes", recipes);
        return "recipes";  // Trả về trang recipes.html
    }
    
    @GetMapping("/{id}")
    public String getRecipeById(@PathVariable Long id, Model model) {
        try {
            RecipeDTO recipe = recipeService.getRecipeById(id);
            model.addAttribute("recipe", recipe);
            return "recipe-detail";  // Trang chi tiết công thức (cần tạo thêm)
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";  // Trang lỗi (cần tạo thêm)
        }
    }
    
    @GetMapping("/ingredient/{ingredientId}")
    public String getRecipesByIngredient(@PathVariable Long ingredientId, Model model) {
        try {
            List<RecipeDTO> recipes = recipeService.getRecipesByIngredientId(ingredientId);
            model.addAttribute("recipes", recipes);
            model.addAttribute("ingredientName", 
                ingredientService.getIngredientById(ingredientId).getName());
            return "recipes";  // Trả về trang recipes.html với filter theo nguyên liệu
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "error";  // Trang lỗi
        }
    }
}