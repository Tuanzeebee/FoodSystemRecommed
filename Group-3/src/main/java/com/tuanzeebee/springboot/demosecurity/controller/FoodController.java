package com.tuanzeebee.springboot.demosecurity.controller;

import com.tuanzeebee.springboot.demosecurity.dao.IngredientDTO;
import com.tuanzeebee.springboot.demosecurity.dao.RecipeDTO;
import com.tuanzeebee.springboot.demosecurity.entity.Ingredient;
import com.tuanzeebee.springboot.demosecurity.repository.IngredientRepository;
import com.tuanzeebee.springboot.demosecurity.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class FoodController {
    @Autowired
    private IngredientRepository ingredientRepo;
    @Autowired
    private RecipeRepository recipeRepo;
    @GetMapping("/ingredients")
    public List<IngredientDTO> getIngredients() {
        return ingredientRepo.findAll().stream()
                .map(i -> new IngredientDTO(i.getId(), i.getName(), i.getIcon()))
                .collect(Collectors.toList());
    }
    @GetMapping("/recipes")
    public List<RecipeDTO> getRecipes() {
        return recipeRepo.findAll().stream()
                .map(r -> new RecipeDTO(
                        r.getId(),
                        r.getName(),
                        r.getDescription(),
                        r.getImage(),
                        r.getIngredients().stream().map(Ingredient::getId).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }
}