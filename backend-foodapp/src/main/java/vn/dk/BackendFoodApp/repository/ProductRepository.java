package vn.dk.BackendFoodApp.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.dk.BackendFoodApp.model.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "SELECT p.*, SUM(od.number_of_products) AS total_sold " +
            "FROM order_details od " +
            "JOIN product p ON od.product_id = p.id " +
            "GROUP BY p.id " +
            "ORDER BY total_sold DESC " +
            "LIMIT 10", nativeQuery = true)
    List<Product> findTop10BestSellingProducts();
}
