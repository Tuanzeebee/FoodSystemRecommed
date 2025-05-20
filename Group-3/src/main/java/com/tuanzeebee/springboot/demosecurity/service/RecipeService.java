package com.tuanzeebee.springboot.demosecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tuanzeebee.springboot.demosecurity.dao.IngredientDTO;
import com.tuanzeebee.springboot.demosecurity.dao.RecipeDTO;
import com.tuanzeebee.springboot.demosecurity.dao.StepDTO;
import com.tuanzeebee.springboot.demosecurity.entity.Ingredient;
import com.tuanzeebee.springboot.demosecurity.entity.Recipe;
import com.tuanzeebee.springboot.demosecurity.entity.Step;
import com.tuanzeebee.springboot.demosecurity.repository.IngredientRepository;
import com.tuanzeebee.springboot.demosecurity.repository.RecipeRepository;
import com.tuanzeebee.springboot.demosecurity.repository.StepRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final StepRepository stepRepository;
    @Autowired
    public RecipeService(RecipeRepository recipeRepository, IngredientRepository ingredientRepository, StepRepository stepRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.stepRepository = stepRepository;
    }
    public List<RecipeDTO> getAllRecipes() {
        return recipeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public RecipeDTO getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
    }
    public RecipeDTO createRecipe(RecipeDTO recipeDTO) {
        Recipe recipe = new Recipe();
        recipe.setName(recipeDTO.getName());
        recipe.setDescription(recipeDTO.getDescription());
        recipe.setImage(recipeDTO.getImage());
        Set<Ingredient> ingredients = new HashSet<>();
        if (recipeDTO.getIngredients() != null) {
            for (IngredientDTO ingredientDTO : recipeDTO.getIngredients()) {
                Ingredient ingredient = ingredientRepository.findById(ingredientDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + ingredientDTO.getId()));
                ingredients.add(ingredient);
            }
        }
        recipe.setIngredients(ingredients);
        Recipe savedRecipe = recipeRepository.save(recipe);
        if (recipeDTO.getSteps() != null) {
            for (StepDTO stepDTO : recipeDTO.getSteps()) {
                Step step = new Step();
                step.setRecipe(savedRecipe);
                step.setStepNumber(stepDTO.getStepNumber());
                step.setDescription(stepDTO.getDescription());
                stepRepository.save(step);
            }
        }
        return convertToDTO(savedRecipe);
    }
    public RecipeDTO updateRecipe(Long id, RecipeDTO recipeDTO) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
        recipe.setName(recipeDTO.getName());
        recipe.setDescription(recipeDTO.getDescription());
        recipe.setImage(recipeDTO.getImage());
        Set<Ingredient> ingredients = new HashSet<>();
        if (recipeDTO.getIngredients() != null) {
            for (IngredientDTO ingredientDTO : recipeDTO.getIngredients()) {
                Ingredient ingredient = ingredientRepository.findById(ingredientDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + ingredientDTO.getId()));
                ingredients.add(ingredient);
            }
        }
        recipe.setIngredients(ingredients);
        Recipe updatedRecipe = recipeRepository.save(recipe);
        // Update steps
        List<Step> existingSteps = stepRepository.findByRecipeIdOrderByStepNumberAsc(id);
        stepRepository.deleteAll(existingSteps);
        if (recipeDTO.getSteps() != null) {
            for (StepDTO stepDTO : recipeDTO.getSteps()) {
                Step step = new Step();
                step.setRecipe(updatedRecipe);
                step.setStepNumber(stepDTO.getStepNumber());
                step.setDescription(stepDTO.getDescription());
                stepRepository.save(step);
            }
        }
        return convertToDTO(updatedRecipe);
    }
    public void deleteRecipe(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy công thức với ID: " + id));
        
        // Xóa các quan hệ
        recipe.getSteps().clear();
        recipe.getIngredients().clear();
        recipe.getPosts().clear();
        recipe.getSavedByUsers().clear();
        
        // Lưu lại để cập nhật các quan hệ
        recipeRepository.save(recipe);
        
        // Sau đó mới xóa recipe
        recipeRepository.deleteById(id);
    }
    public List<RecipeDTO> getRecipesByIngredientId(Long ingredientId) {
        return recipeRepository.findByIngredientId(ingredientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    private RecipeDTO convertToDTO(Recipe recipe) {
        RecipeDTO dto = new RecipeDTO();
        dto.setId(recipe.getId());
        dto.setName(recipe.getName());
        dto.setDescription(recipe.getDescription());
        dto.setImage(recipe.getImage());
        Set<IngredientDTO> ingredientDTOs = recipe.getIngredients().stream()
                .map(ingredient -> {
                    IngredientDTO ingredientDTO = new IngredientDTO();
                    ingredientDTO.setId(ingredient.getId());
                    ingredientDTO.setName(ingredient.getName());
                    ingredientDTO.setIcon(ingredient.getIcon());
                    return ingredientDTO;
                })
                .collect(Collectors.toSet());
        dto.setIngredients(ingredientDTOs);
        List<Step> steps = stepRepository.findByRecipeIdOrderByStepNumberAsc(recipe.getId());
        Set<StepDTO> stepDTOs = steps.stream()
                .map(step -> {
                    StepDTO stepDTO = new StepDTO();
                    stepDTO.setId(step.getId());
                    stepDTO.setStepNumber(step.getStepNumber());
                    stepDTO.setDescription(step.getDescription());
                    return stepDTO;
                })
                .collect(Collectors.toSet());
        dto.setSteps(stepDTOs);
        return dto;
    }
    
    public long countRecipes() {
        return recipeRepository.count();
    }
}