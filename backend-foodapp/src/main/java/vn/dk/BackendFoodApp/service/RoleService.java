package vn.dk.BackendFoodApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.dk.BackendFoodApp.model.Role;
import vn.dk.BackendFoodApp.repository.RoleRepository;

import java.util.function.Supplier;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public Role getRoleByName(String name)
    {
        return roleRepository.findByName(name).orElseThrow(new Supplier<RuntimeException>() {
            @Override
            public RuntimeException get() {
                return new RuntimeException("Role not found" + name);
            }
        });
    }
}
