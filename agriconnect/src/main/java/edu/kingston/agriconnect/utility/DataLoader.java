package edu.kingston.agriconnect.utility;

import edu.kingston.agriconnect.model.Role;
import edu.kingston.agriconnect.model.enums.RoleName;
import edu.kingston.agriconnect.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override

    public void run(String... args) throws Exception {
        createRoleIfNotFound(RoleName.ROLE_FARMER);
        createRoleIfNotFound(RoleName.ROLE_BUYER);
        createRoleIfNotFound(RoleName.ROLE_ADMIN);
    }

    private void createRoleIfNotFound(RoleName roleName) {
        if (!roleRepository.findByRoleName(roleName).isPresent()) {
            Role role = new Role();
            role.setRoleName(roleName);
            roleRepository.save(role);
        }
    }
}