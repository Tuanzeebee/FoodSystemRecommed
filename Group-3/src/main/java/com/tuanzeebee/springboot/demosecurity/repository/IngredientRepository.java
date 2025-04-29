package com.tuanzeebee.springboot.demosecurity.repository;

import com.tuanzeebee.springboot.demosecurity.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long>{
}
