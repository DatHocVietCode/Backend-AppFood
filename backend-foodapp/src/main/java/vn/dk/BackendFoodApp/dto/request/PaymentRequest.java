package vn.dk.BackendFoodApp.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @NotBlank(message = "Shipping method is required")
    private String shippingMethod;

    private Long idVoucher; // Optional - Có thể null

    @NotNull(message = "Address ID is required")
    private Long idAddress;

    public PaymentRequest() {
    }

    public PaymentRequest(String paymentMethod, String shippingMethod, Long idVoucher, Long idAddress) {
        this.paymentMethod = paymentMethod;
        this.shippingMethod = shippingMethod;
        this.idVoucher = idVoucher;
        this.idAddress = idAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public Long getIdVoucher() {
        return idVoucher;
    }

    public void setIdVoucher(Long idVoucher) {
        this.idVoucher = idVoucher;
    }

    public Long getIdAddress() {
        return idAddress;
    }

    public void setIdAddress(Long idAddress) {
        this.idAddress = idAddress;
    }
}

