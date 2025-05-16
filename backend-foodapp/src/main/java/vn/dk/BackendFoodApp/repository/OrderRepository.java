package vn.dk.BackendFoodApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.dk.BackendFoodApp.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdAndStatusOrderByOrderDateDesc(Long userId, String status);
    List<Order> findByUserIdAndStatusInOrderByOrderDateDesc(Long userId, List<String> statuses);
}
