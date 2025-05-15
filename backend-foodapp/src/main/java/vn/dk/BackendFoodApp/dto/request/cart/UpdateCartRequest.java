package vn.dk.BackendFoodApp.dto.request.cart;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

@Data
public class UpdateCartRequest {
    @NotNull
    @Min(value = 1)
    Long productId;
    @NonNull
    @Min(value = 1)
    Integer quantity;
}
