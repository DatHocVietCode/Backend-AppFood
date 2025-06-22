package vn.dk.BackendFoodApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.dk.BackendFoodApp.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User findByEmail(String email);

    User findByUsername(String username);

    User findByRefreshTokenAndUsername(String token, String username);

    @Query("SELECT u.refreshToken FROM User u WHERE u.username = :username")
    Optional<String> findOptionalRefreshTokenByUsername(@Param("username") String username);

}
