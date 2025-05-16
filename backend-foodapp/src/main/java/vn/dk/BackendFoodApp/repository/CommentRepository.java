package vn.dk.BackendFoodApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.dk.BackendFoodApp.model.Comment;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
