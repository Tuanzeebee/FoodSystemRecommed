package com.tuanzeebee.springboot.demosecurity.repository;

import com.tuanzeebee.springboot.demosecurity.entity.Role;

public interface RoleDao {

    public Role findRoleByName(String theRoleName);
}
