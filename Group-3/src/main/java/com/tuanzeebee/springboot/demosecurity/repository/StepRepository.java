package com.tuanzeebee.springboot.demosecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tuanzeebee.springboot.demosecurity.entity.Step;

import java.util.List;
public interface StepRepository extends JpaRepository<Step, Long> {
    List<Step> findByRecipeIdOrderByStepNumberAsc(Long recipeId);
}
