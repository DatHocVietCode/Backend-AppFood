package vn.dk.BackendFoodApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductInOrderDTO {
    private Long idProduct;           // Thêm id sản phẩm
    private String foodName;
    private String category;
    private int quantity;
    private String thumbnail;
    private Float price;
}
