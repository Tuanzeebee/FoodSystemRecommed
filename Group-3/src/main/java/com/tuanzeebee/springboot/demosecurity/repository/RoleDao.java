package com.tuanzeebee.springboot.demosecurity.repository;

import java.util.List;

import com.tuanzeebee.springboot.demosecurity.entity.Role;

public interface RoleDao {

    Role findRoleByName(String theRoleName);
    
    List<Role> findAllRoles();
}
