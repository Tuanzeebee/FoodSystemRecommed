package com.tuanzeebee.springboot.demosecurity.dao;

import java.util.List;

public class RecipeDTO {

    private Long id;
    private String name;
    private String description;
    private String image;
    private List<Long> ingredients;

    public RecipeDTO(Long id, String name, String description, String image, List<Long> ingredients) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.ingredients = ingredients;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Long> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Long> ingredients) {
        this.ingredients = ingredients;
    }
}
