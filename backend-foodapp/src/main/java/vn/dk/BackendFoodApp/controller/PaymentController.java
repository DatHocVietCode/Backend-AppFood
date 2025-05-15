package vn.dk.BackendFoodApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.dk.BackendFoodApp.dto.ResponseObject;
import vn.dk.BackendFoodApp.dto.request.PaymentRequest;
import vn.dk.BackendFoodApp.dto.response.BaseResponse;
import vn.dk.BackendFoodApp.model.*;
import vn.dk.BackendFoodApp.service.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    TokenService tokenService;

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    @Autowired
    CartService cartService;

    @Autowired
    VoucherService voucherService;
    @PostMapping
    public ResponseEntity<ResponseObject> makePayment(@RequestBody PaymentRequest request) {
        Optional<String> usernameOpt = tokenService.getCurrentUserLogin();

        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseObject.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("Unauthorized: User not logged in")
                            .data(null)
                            .build()
            );
        }

        String username = usernameOpt.get();
        User user = userService.handleGetUserByUserName(username);

        Order order = new Order();
        order.setUser(user);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setShippingMethod(orderService.findShippingMethodByName(request.getShippingMethod()));
        order.setVoucher(orderService.findVoucherById(request.getIdVoucher()));
        order.setAddress(orderService.findAddressById(request.getIdAddress()));
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("Pending");
        order.setActive(true);

        // Lấy cart hiện tại của user
        Cart cart = cartService.getCartByUser(user);
        List<CartItem> cartItems = cart.getCartItems();

        List<OrderDetail> orderDetails = new ArrayList<>();
        float totalMoney = 0f;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();
            float price = product.getPrice();
            float total = price * quantity;

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setPrice(price);
            detail.setNumberOfProducts(quantity);
            detail.setTotalMoney(total);
            detail.setVoucher(order.getVoucher());

            orderDetails.add(detail);
            totalMoney += total;
        }

        // Cộng phí shipping nếu có
        if (order.getShippingMethod() != null) {
            totalMoney += order.getShippingMethod().getPrice();
        }

        // Trừ tiền voucher nếu có
        if (order.getVoucher() != null && totalMoney >= order.getVoucher().getDiscount()) {
            totalMoney -= order.getVoucher().getDiscount();

            // Voucher thành inactive
            order.getVoucher().setActive(false);
            orderService.saveVoucher(order.getVoucher());

            if (totalMoney < 0) totalMoney = 0;
        }

        order.setOrderDetails(orderDetails);
        order.setTotalMoney(totalMoney);

        try {
            orderService.createOrder(order);
            // Xóa cart hoặc cartItems sau khi order thành công (nếu cần)
            cart.getCartItems().clear();
            cartService.save(cart);

            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK.value())
                            .message("Payment and order created successfully")
                            .data(null)
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error processing order: " + e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }
}
