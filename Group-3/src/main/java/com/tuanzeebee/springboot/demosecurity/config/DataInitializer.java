package com.tuanzeebee.springboot.demosecurity.config;

import com.tuanzeebee.springboot.demosecurity.entity.Role;
import com.tuanzeebee.springboot.demosecurity.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Autowired
    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        // Tạo các role mặc định nếu chúng chưa tồn tại
        createRoleIfNotFound("USER");
        createRoleIfNotFound("MANAGER");
        createRoleIfNotFound("ADMIN");
    }

    private void createRoleIfNotFound(String name) {
        if (!roleRepository.findByName(name).isPresent()) {
            Role role = new Role();
            role.setName(name);
            roleRepository.save(role);
        }
    }
}
