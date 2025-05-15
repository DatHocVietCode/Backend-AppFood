package vn.dk.BackendFoodApp.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.dk.BackendFoodApp.dto.ResponseObject;
import vn.dk.BackendFoodApp.dto.request.cart.UpdateCartRequest;
import vn.dk.BackendFoodApp.dto.response.cartitem.CartItemDTO;
import vn.dk.BackendFoodApp.dto.response.voucher.VoucherResponse;
import vn.dk.BackendFoodApp.model.Cart;
import vn.dk.BackendFoodApp.model.CartItem;
import vn.dk.BackendFoodApp.model.Product;
import vn.dk.BackendFoodApp.model.User;
import vn.dk.BackendFoodApp.service.CartService;
import vn.dk.BackendFoodApp.service.ProductService;
import vn.dk.BackendFoodApp.service.TokenService;
import vn.dk.BackendFoodApp.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/carts")
public class CartController {

    @Autowired
    CartService cartService;

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @PutMapping("/update")
    public ResponseEntity<ResponseObject> addToCart(@RequestBody @Valid UpdateCartRequest req) {
        Optional<String> usernameOpt = TokenService.getCurrentUserLogin();

        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ResponseObject.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .data(null)
                            .message("Unauthorized")
                            .build());
        }

        try {
            cartService.addOrUpdateProductInCart(usernameOpt.get(), req.getProductId(), req.getQuantity());

            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK.value())
                    .message("Product added to cart successfully.")
                    .data(null)
                    .build());

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ResponseObject> removeProductFromCart(@PathVariable Long productId) {
        Optional<String> usernameOpt = TokenService.getCurrentUserLogin();

        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ResponseObject.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("Unauthorized")
                            .data(null)
                            .build());
        }

        try {
            cartService.removeProductFromCart(usernameOpt.get(), productId);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK.value())
                    .message("Product removed from cart successfully.")
                    .data(null)
                    .build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message(e.getMessage())
                            .data(null)
                            .build());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseObject> addProductToCart(@RequestBody @Valid UpdateCartRequest req) {
        Optional<String> usernameOpt = TokenService.getCurrentUserLogin();

        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ResponseObject.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .data(null)
                            .message("Unauthorized")
                            .build());
        }

        try {
            cartService.addProductToCart(usernameOpt.get(), req.getProductId(), req.getQuantity());

            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK.value())
                    .message("Product added to cart successfully.")
                    .data(null)
                    .build());

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message(e.getMessage())
                            .data(null)
                            .build());
        }
    }
    @GetMapping("")
    public ResponseEntity<ResponseObject> listProductInCart() {
        User user = userService.getUserByToken();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .data(null)
                            .message("UnAuthorized")
                            .build());
        }

        Cart cart = cartService.getCartByUser(user);

        if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK.value())
                            .data(Collections.emptyList())
                            .message("Cart is empty")
                            .build()
            );
        }

        List<CartItemDTO> cartItemDTOs = cart.getCartItems().stream()
                .map(item -> CartItemDTO.builder()
                        .productId(item.getProduct().getId())
                        .name(item.getProduct().getName())
                        .price(item.getProduct().getPrice())
                        .thumbnail(item.getProduct().getThumbnail())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK.value())
                        .data(cartItemDTOs)
                        .message("Get list product in cart success")
                        .build()
        );
    }
}
