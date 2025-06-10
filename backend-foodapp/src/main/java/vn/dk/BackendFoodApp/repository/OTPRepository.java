package vn.dk.BackendFoodApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.dk.BackendFoodApp.model.OTP;

import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, String> {
    Optional<OTP> findByEmail(String email);
}
