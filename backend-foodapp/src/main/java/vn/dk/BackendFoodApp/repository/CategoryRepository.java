package vn.dk.BackendFoodApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.dk.BackendFoodApp.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
