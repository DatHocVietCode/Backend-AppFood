package vn.dk.BackendFoodApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.dk.BackendFoodApp.dto.ResponseObject;
import vn.dk.BackendFoodApp.dto.request.PaymentReOrderReq;
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
        order.setStatus("0");
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
//            detail.setVoucher(order.getVoucher());

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
    @PostMapping("re-order")
    public ResponseEntity<ResponseObject> makePayment(@RequestBody PaymentReOrderReq request) {
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

        // Lấy đơn hàng cũ
        Order oldOrder = orderService.findById(request.getIdOrder());
        if (oldOrder == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("Original order not found")
                            .data(null)
                            .build()
            );
        }

        // Tạo đơn hàng mới
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setPaymentMethod(request.getPaymentMethod());
        newOrder.setShippingMethod(orderService.findShippingMethodByName(request.getShippingMethod()));
        newOrder.setVoucher(orderService.findVoucherById(request.getIdVoucher()));
        newOrder.setAddress(orderService.findAddressById(request.getIdAddress()));
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setStatus("0");
        newOrder.setActive(true);

        List<OrderDetail> orderDetails = new ArrayList<>();
        float totalMoney = 0f;

        for (OrderDetail oldDetail : oldOrder.getOrderDetails()) {
            Product product = oldDetail.getProduct();
            int quantity = oldDetail.getNumberOfProducts();
            float price = product.getPrice();
            float total = price * quantity;

            OrderDetail detail = new OrderDetail();
            detail.setOrder(newOrder);
            detail.setProduct(product);
            detail.setPrice(price);
            detail.setNumberOfProducts(quantity);
            detail.setTotalMoney(total);

            orderDetails.add(detail);
            totalMoney += total;
        }

        // Cộng phí shipping nếu có
        if (newOrder.getShippingMethod() != null) {
            totalMoney += newOrder.getShippingMethod().getPrice();
        }

        // Trừ tiền voucher nếu có
        if (newOrder.getVoucher() != null && totalMoney >= newOrder.getVoucher().getDiscount()) {
            totalMoney -= newOrder.getVoucher().getDiscount();

            newOrder.getVoucher().setActive(false);
            orderService.saveVoucher(newOrder.getVoucher());

            if (totalMoney < 0) totalMoney = 0;
        }

        newOrder.setOrderDetails(orderDetails);
        newOrder.setTotalMoney(totalMoney);

        try {
            orderService.createOrder(newOrder);

            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK.value())
                            .message("Re-order created successfully")
                            .data(null)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error processing re-order: " + e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }

}
