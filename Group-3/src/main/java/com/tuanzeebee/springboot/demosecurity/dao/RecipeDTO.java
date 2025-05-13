package com.tuanzeebee.springboot.demosecurity.dao;

import lombok.Data;
import java.util.Set;
@Data
public class RecipeDTO {
    private Long id;
    private String name;
    private String description;
    private String image;
    private Set<IngredientDTO> ingredients;
    private Set<StepDTO> steps;
    private Boolean isSavedByCurrentUser;
}