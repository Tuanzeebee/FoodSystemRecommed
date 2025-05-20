package com.tuanzeebee.springboot.demosecurity.entity;

import jakarta.persistence.*;

import java.util.Collection;

import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    private Boolean enabled = true;
    
    private String firstName;
    
    private String lastName;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "avatar")
    private String avatar;
    
    @Column(name = "bio", length = 1000)
    private String bio;
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
        name = "users_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    
    @OneToMany(mappedBy = "user")
    private Set<Post> posts = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "saved_recipes",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "recipe_id")
    )
    private Set<Recipe> savedRecipes = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "follows",
        joinColumns = @JoinColumn(name = "follower_id"),
        inverseJoinColumns = @JoinColumn(name = "followed_id")
    )
    private Set<User> following = new HashSet<>();
    
    @ManyToMany(mappedBy = "following")
    private Set<User> followers = new HashSet<>();

    
}
