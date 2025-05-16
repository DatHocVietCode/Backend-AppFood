package vn.dk.BackendFoodApp.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.dk.BackendFoodApp.dto.ResponseObject;
import vn.dk.BackendFoodApp.dto.request.CommentRequestDTO;
import vn.dk.BackendFoodApp.model.Comment;
import vn.dk.BackendFoodApp.model.Product;
import vn.dk.BackendFoodApp.model.User;
import vn.dk.BackendFoodApp.repository.CommentRepository;
import vn.dk.BackendFoodApp.repository.ProductRepository;
import vn.dk.BackendFoodApp.service.TokenService;
import vn.dk.BackendFoodApp.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<ResponseObject> postComment(@RequestBody CommentRequestDTO commentRequest) {
        Optional<String> usernameOpt = TokenService.getCurrentUserLogin();

        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("Unauthorized")
                            .data(null)
                            .build());
        }

        User user = userService.handleGetUserByUserName(usernameOpt.get());

        Optional<Product> productOpt = productRepository.findById(commentRequest.getProductId());
        if (productOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("Product not found")
                            .data(null)
                            .build());
        }

        Comment comment = Comment.builder()
                .content(commentRequest.getContent())
                .star(commentRequest.getStar())
                .user(user)
                .product(productOpt.get())
                .build();

        commentRepository.save(comment);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Comment posted successfully")
                        .data(comment)
                        .build());
    }
}
