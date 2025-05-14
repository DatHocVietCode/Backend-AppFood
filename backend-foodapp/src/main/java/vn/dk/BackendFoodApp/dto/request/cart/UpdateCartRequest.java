package vn.dk.BackendFoodApp.dto.request.cart;


import lombok.Data;

@Data
public class UpdateCartRequest {
    Long productId;
    Integer quantity;
}
