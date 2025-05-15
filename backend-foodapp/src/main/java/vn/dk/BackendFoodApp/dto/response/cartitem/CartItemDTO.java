package vn.dk.BackendFoodApp.dto.response.cartitem;

import lombok.*;
import vn.dk.BackendFoodApp.model.CartItem;
import vn.dk.BackendFoodApp.model.Product;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemDTO {
    private Long productId;
    private String name;
    private Float price;
    private String thumbnail;
    private Integer quantity;

    public static CartItemDTO fromCartItem(CartItem cartItem) {
        Product product = cartItem.getProduct();
        return CartItemDTO.builder()
                .productId(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .quantity(cartItem.getQuantity())
                .build();
    }
}