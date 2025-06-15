package vn.dk.BackendFoodApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.dk.BackendFoodApp.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
