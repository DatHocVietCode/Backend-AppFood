package vn.dk.BackendFoodApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.dk.BackendFoodApp.model.Address;
import vn.dk.BackendFoodApp.model.User;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
    Address findByUserUsernameAndIsDefaultTrue(String username);
}
