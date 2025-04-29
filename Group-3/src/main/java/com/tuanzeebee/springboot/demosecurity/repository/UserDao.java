package com.tuanzeebee.springboot.demosecurity.repository;

import com.tuanzeebee.springboot.demosecurity.entity.User;

public interface UserDao {

    User findByUserName(String userName);

    void save(User theUser);
}
