package com.tuanzeebee.springboot.demosecurity.entity;

import jakarta.persistence.*;
import lombok.Data;
@Data  // If using Lombok
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
}