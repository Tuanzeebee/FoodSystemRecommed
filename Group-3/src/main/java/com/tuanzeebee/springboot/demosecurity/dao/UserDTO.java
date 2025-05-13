package com.tuanzeebee.springboot.demosecurity.dao;

import lombok.Data;
import java.util.Set;
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String avatar;
    private String bio;
    private Set<String> roles;
}