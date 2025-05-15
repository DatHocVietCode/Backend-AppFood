package vn.dk.BackendFoodApp.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.dk.BackendFoodApp.model.Cart;
import vn.dk.BackendFoodApp.model.CartItem;
import vn.dk.BackendFoodApp.model.Product;
import vn.dk.BackendFoodApp.model.User;
import vn.dk.BackendFoodApp.repository.CartItemRepository;
import vn.dk.BackendFoodApp.repository.CartRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final UserService userService;
    private final ProductService productService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    // Tạo cart mới cho user mới đăng ký
    public Cart createCartForUser(User user) {
        Cart cart = Cart.builder()
                .user(user)
                .cartItems(new ArrayList<>())
                .build();
        return cartRepository.save(cart);
    }

    // Lấy cart của user, chắc chắn tồn tại vì đã tạo lúc đăng ký
    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user: " + user.getUsername()));
    }

    public void addOrUpdateProductInCart(String username, Long productId, int quantity) {
        User user = userService.handleGetUserByUserName(username);

        Cart cart = getCartByUser(user);  // luôn có cart rồi

        Optional<CartItem> existingItemOpt = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(quantity);
        } else {
            Product product = productService.getProductById(productId);
            if (product == null) {
                throw new EntityNotFoundException("Product not found");
            }
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();
            cart.getCartItems().add(newItem);
        }

        cartRepository.save(cart);
    }

    public void removeProductFromCart(String username, Long productId) {
        User user = userService.handleGetUserByUserName(username);
        Cart cart = getCartByUser(user);

        boolean removed = cart.getCartItems().removeIf(item -> item.getProduct().getId().equals(productId));

        if (!removed) {
            throw new EntityNotFoundException("Product not found in cart");
        }

        cartRepository.save(cart);
    }

    public void save(Cart cart) {
        cartRepository.save(cart);
    }
}
