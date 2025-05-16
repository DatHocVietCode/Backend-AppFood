package vn.dk.BackendFoodApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.dk.BackendFoodApp.dto.ResponseObject;
import vn.dk.BackendFoodApp.model.User;
import vn.dk.BackendFoodApp.model.Voucher;
import vn.dk.BackendFoodApp.service.UserService;
import vn.dk.BackendFoodApp.service.VoucherService;

import java.security.SecureRandom;

@RestController
@RequestMapping("/vouchers")
public class VoucherController {

    @Autowired
    UserService userService;

    @Autowired
    VoucherService voucherService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 9;
    private static final SecureRandom random = new SecureRandom();

    // Hàm tạo code voucher ngẫu nhiên
    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }


    @PostMapping("/add")
    public ResponseEntity<ResponseObject> addVoucher(Integer discount) {
        User user = userService.getUserByToken();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseObject.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("Unauthorized")
                            .data(null)
                            .build());
        }

        Voucher voucher = new Voucher();
        voucher.setDiscount(discount);
        voucher.setUser(user);
        voucher.setActive(true);
        voucher.setMinAmount(0);
        voucher.setName("Hoan tien thanh toán");

        // Gán code ngẫu nhiên
        voucher.setCode(generateRandomCode());

        voucherService.updateVoucher(voucher);  // hoặc saveVoucher

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject.builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Voucher added successfully")
                        .data(null)
                        .build());
    }
}
