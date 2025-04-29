package com.tuanzeebee.springboot.demosecurity.repository;

import com.tuanzeebee.springboot.demosecurity.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}
