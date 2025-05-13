package com.tuanzeebee.springboot.demosecurity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tuanzeebee.springboot.demosecurity.dao.RecipeDTO;
import com.tuanzeebee.springboot.demosecurity.service.RecipeService;

import java.util.List;
@RestController
@RequestMapping("/api/recipes")
public class RecipeControllerAPI {
    private final RecipeService recipeService;
    
    @Autowired
    public RecipeControllerAPI(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    
    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.getRecipeById(id));
    }
    
    @GetMapping("/ingredient/{ingredientId}")
    public ResponseEntity<List<RecipeDTO>> getRecipesByIngredientId(@PathVariable Long ingredientId) {
        return ResponseEntity.ok(recipeService.getRecipesByIngredientId(ingredientId));
    }
    
    @PostMapping
    public ResponseEntity<RecipeDTO> createRecipe(@RequestBody RecipeDTO recipeDTO) {
        return new ResponseEntity<>(recipeService.createRecipe(recipeDTO), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RecipeDTO> updateRecipe(@PathVariable Long id, @RequestBody RecipeDTO recipeDTO) {
        return ResponseEntity.ok(recipeService.updateRecipe(id, recipeDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }
}
