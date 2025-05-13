package vn.dk.BackendFoodApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.dk.BackendFoodApp.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
