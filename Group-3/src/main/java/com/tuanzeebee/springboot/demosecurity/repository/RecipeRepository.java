package com.tuanzeebee.springboot.demosecurity.repository;

import com.tuanzeebee.springboot.demosecurity.entity.Recipe;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("SELECT r FROM Recipe r JOIN r.ingredients i WHERE i.id = :ingredientId")
    List<Recipe> findByIngredientId(Long ingredientId);
}
