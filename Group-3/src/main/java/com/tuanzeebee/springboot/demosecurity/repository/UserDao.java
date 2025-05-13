package com.tuanzeebee.springboot.demosecurity.repository;

import java.util.List;

import com.tuanzeebee.springboot.demosecurity.entity.User;

public interface UserDao {

    User findByUserName(String userName);

    void save(User theUser);
    
    List<User> findAllUsers();
    
    User findById(Long id);
    
    void deleteById(Long id);
}
