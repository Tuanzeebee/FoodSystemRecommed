package com.tuanzeebee.springboot.demosecurity.entity;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "ingredients")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String icon;
}