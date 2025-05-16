package vn.dk.BackendFoodApp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentReOrderReq {

    private String paymentMethod;
    private String shippingMethod;
    private Long idVoucher;
    private Long idAddress;

    private Long idOrder;
}