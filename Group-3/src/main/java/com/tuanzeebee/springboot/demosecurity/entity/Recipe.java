package com.tuanzeebee.springboot.demosecurity.entity;

import jakarta.persistence.*;

import lombok.Data;
import java.util.HashSet;
import java.util.Set;
@Data
@Entity
@Table(name = "recipes")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String image;
    
    @ManyToMany
    @JoinTable(
        name = "recipe_ingredients",
        joinColumns = @JoinColumn(name = "recipe_id"),
        inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private Set<Ingredient> ingredients = new HashSet<>();
    
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    @OrderBy("stepNumber ASC")
    private Set<Step> steps = new HashSet<>();
    
    @OneToMany(mappedBy = "recipe")
    private Set<Post> posts = new HashSet<>();
    
    @ManyToMany(mappedBy = "savedRecipes")
    private Set<User> savedByUsers = new HashSet<>();

}
