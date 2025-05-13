package com.tuanzeebee.springboot.demosecurity.entity;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "steps")
public class Step {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
    
    @Column(nullable = false)
    private Integer stepNumber;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
}