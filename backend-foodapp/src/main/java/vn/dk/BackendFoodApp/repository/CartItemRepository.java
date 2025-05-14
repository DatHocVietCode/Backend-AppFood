package vn.dk.BackendFoodApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.dk.BackendFoodApp.model.Cart;
import vn.dk.BackendFoodApp.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

}

