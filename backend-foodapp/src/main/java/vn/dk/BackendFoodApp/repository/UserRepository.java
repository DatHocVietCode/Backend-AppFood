package vn.dk.BackendFoodApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.dk.BackendFoodApp.model.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User findByEmail(String email);

    User findByUsername(String username);

    User findByRefreshTokenAndUsername(String token, String username);
}
