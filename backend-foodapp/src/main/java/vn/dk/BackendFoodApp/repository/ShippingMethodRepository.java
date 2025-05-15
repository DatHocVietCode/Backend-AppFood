package vn.dk.BackendFoodApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.dk.BackendFoodApp.model.ShippingMethod;

import java.util.Optional;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethod,Long> {

    Optional<ShippingMethod> findByMethodName(String methodName);
}
