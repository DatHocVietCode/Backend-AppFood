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

    public void addOrUpdateProductInCart(String username, Long productId, int quantity) {
        User user = userService.handleGetUserByUserName(username);
        Product product = productService.getProductById(productId);

        if (product == null) {
            throw new EntityNotFoundException("Product not found");
        }

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .cartItems(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });

        // Tìm CartItem theo product
        Optional<CartItem> existingItemOpt = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            // Cập nhật quantity bằng giá trị mới
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(quantity);
        } else {
            // Thêm mới
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

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user"));

        List<CartItem> items = cart.getCartItems();

        boolean removed = items.removeIf(item -> item.getProduct().getId().equals(productId));

        if (!removed) {
            throw new EntityNotFoundException("Product not found in cart");
        }

        cartRepository.save(cart); // do cascade = ALL, cartItem sẽ bị xóa
    }

    public Optional<Cart> getCartByUser(User user) {
        return cartRepository.findByUser(user);
    }

    public Cart save(Cart cart) {
        return cartRepository.save(cart);
    }

}
