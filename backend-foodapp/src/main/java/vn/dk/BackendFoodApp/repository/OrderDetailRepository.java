package vn.dk.BackendFoodApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.dk.BackendFoodApp.model.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail,Long> {

}
