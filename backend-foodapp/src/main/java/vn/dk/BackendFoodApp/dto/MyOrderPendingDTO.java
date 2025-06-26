package vn.dk.BackendFoodApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.dk.BackendFoodApp.model.Address;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyOrderPendingDTO {
    private Long idOrder;
    private Float totalPrice;
    private List<ProductInOrderDTO> products;
    private String status;
    private LocalDateTime created;
    private String paymentMethod;
    private Float deliveryFee;
    private Integer voucher;
    private String address;
}