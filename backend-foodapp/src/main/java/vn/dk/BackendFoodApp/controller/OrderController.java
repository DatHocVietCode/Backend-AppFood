package vn.dk.BackendFoodApp.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.dk.BackendFoodApp.dto.MyOrderPendingDTO;
import vn.dk.BackendFoodApp.dto.ProductInOrderDTO;
import vn.dk.BackendFoodApp.dto.ResponseObject;
import vn.dk.BackendFoodApp.model.Order;
import vn.dk.BackendFoodApp.model.OrderDetail;
import vn.dk.BackendFoodApp.model.Product;
import vn.dk.BackendFoodApp.model.User;
import vn.dk.BackendFoodApp.repository.OrderRepository;
import vn.dk.BackendFoodApp.service.TokenService;
import vn.dk.BackendFoodApp.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;

    private final UserService userService;

    @GetMapping("/my-orders/pending")
    public ResponseEntity<ResponseObject> getAllMyPendingOrders() {
        Optional<String> usernameOpt = TokenService.getCurrentUserLogin();

        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ResponseObject.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .data(null)
                            .message("UnAuthorized")
                            .build()
            );
        }

        User user = userService.handleGetUserByUserName(usernameOpt.get());
        List<Order> pendingOrders = orderRepository.findByUserIdAndStatusOrderByOrderDateDesc(user.getId(), "PENDING");

        List<MyOrderPendingDTO> result = new ArrayList<>();

        for (Order order : pendingOrders) {
            List<ProductInOrderDTO> products = new ArrayList<>();

            for (OrderDetail detail : order.getOrderDetails()) {
                Product product = detail.getProduct();

                ProductInOrderDTO productDTO = ProductInOrderDTO.builder()
                        .foodName(product.getName())
                        .category(product.getCategory() != null ? product.getCategory().getName() : null)
                        .quantity(detail.getNumberOfProducts())
                        .thumbnail(product.getThumbnail())
                        .build();

                products.add(productDTO);
            }

            MyOrderPendingDTO dto = MyOrderPendingDTO.builder()
                    .idOrder(order.getId())
                    .status(order.getStatus())
                    .totalPrice(order.getTotalMoney())
                    .created(order.getOrderDate())
                    .products(products)
                    .build();

            result.add(dto);
        }

        ResponseObject response = ResponseObject.builder()
                .message("Get pending orders successfully")
                .status(HttpStatus.OK.value())
                .data(result)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-orders/history")
    public ResponseEntity<ResponseObject> getAllMyCompletedOrCancelledOrders() {
        Optional<String> usernameOpt = TokenService.getCurrentUserLogin();

        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ResponseObject.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .data(null)
                            .message("UnAuthorized")
                            .build()
            );
        }

        User user = userService.handleGetUserByUserName(usernameOpt.get());

        // ✅ Lấy cả COMPLETED và CANCEL
        List<Order> completedOrCancelledOrders = orderRepository
                .findByUserIdAndStatusInOrderByOrderDateDesc(user.getId(), List.of("COMPLETED", "CANCEL"));

        List<MyOrderPendingDTO> result = new ArrayList<>();

        for (Order order : completedOrCancelledOrders) {
            List<ProductInOrderDTO> products = new ArrayList<>();

            for (OrderDetail detail : order.getOrderDetails()) {
                Product product = detail.getProduct();

                ProductInOrderDTO productDTO = ProductInOrderDTO.builder()
                        .idProduct(product.getId())
                        .foodName(product.getName())
                        .category(product.getCategory() != null ? product.getCategory().getName() : null)
                        .quantity(detail.getNumberOfProducts())
                        .thumbnail(product.getThumbnail())
                        .build();

                products.add(productDTO);
            }

            MyOrderPendingDTO dto = MyOrderPendingDTO.builder()
                    .idOrder(order.getId())
                    .totalPrice(order.getTotalMoney())
                    .products(products)
                    .created(order.getOrderDate())
                    .status(order.getStatus())
                    .build();

            result.add(dto);
        }

        ResponseObject response = ResponseObject.builder()
                .message("Get completed or cancelled orders successfully")
                .status(HttpStatus.OK.value())
                .data(result)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<ResponseObject> cancelMyOrder(@PathVariable Long orderId) {
        Optional<String> usernameOpt = TokenService.getCurrentUserLogin();

        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ResponseObject.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .data(null)
                            .message("UnAuthorized")
                            .build()
            );
        }

        User user = userService.handleGetUserByUserName(usernameOpt.get());

        Optional<Order> orderOpt = orderRepository.findById(orderId);

        if (orderOpt.isEmpty()) {
            return ResponseEntity.status(404).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .data(null)
                            .message("Order not found")
                            .build()
            );
        }

        Order order = orderOpt.get();

        // ✅ Kiểm tra quyền sở hữu
        if (!order.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body(
                    ResponseObject.builder()
                            .status(HttpStatus.FORBIDDEN.value())
                            .data(null)
                            .message("You are not authorized to cancel this order")
                            .build()
            );
        }

        // ✅ Chỉ cho phép hủy nếu chưa giao hàng xong
        if (order.getStatus().equalsIgnoreCase("COMPLETED") || order.getStatus().equalsIgnoreCase("CANCEL")) {
            return ResponseEntity.status(400).body(
                    ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST.value())
                            .data(null)
                            .message("Order cannot be cancelled")
                            .build()
            );
        }

        order.setStatus("CANCEL");
        orderRepository.save(order);

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK.value())
                        .message("Order cancelled successfully")
                        .data(null)
                        .build()
        );
    }
}
