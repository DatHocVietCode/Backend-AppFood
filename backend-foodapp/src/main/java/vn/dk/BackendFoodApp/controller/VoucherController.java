package vn.dk.BackendFoodApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.dk.BackendFoodApp.dto.ResponseObject;
import vn.dk.BackendFoodApp.dto.response.voucher.VoucherResponse;
import vn.dk.BackendFoodApp.model.User;
import vn.dk.BackendFoodApp.model.Voucher;
import vn.dk.BackendFoodApp.service.TokenService;
import vn.dk.BackendFoodApp.service.UserService;
import vn.dk.BackendFoodApp.service.VoucherService;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/vouchers")
public class VoucherController {

    @Autowired
    VoucherService voucherService;
    @Autowired
    UserService userService;


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
    @GetMapping("/available")
    public ResponseEntity<ResponseObject> getListVoucherAvailable(){
       List<VoucherResponse> vouchers = voucherService.getListVoucher();
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK.value())
                .message("Get list of vouchers available successfully")
                .data(vouchers)
                .build());
    }

    @GetMapping("/my-vouchers")
    public ResponseEntity<ResponseObject> getListMyVoucher(){
        Optional<String> usernameOpt = TokenService.getCurrentUserLogin();

        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(401).body(ResponseObject.builder().status(HttpStatus.UNAUTHORIZED.value()).data(null).message("UnAuthorized").build());
        }
        User user = userService.handleGetUserByUserName(usernameOpt.get());

        List<VoucherResponse> vouchers = voucherService.getActiveVouchersByUserId(user.getId());
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK.value())
                .message("Get list of vouchers available successfully")
                .data(vouchers)
                .build());
    }
    @PutMapping("/update/{idVoucher}")
    public ResponseEntity<ResponseObject> assignUserToVoucher(
            @PathVariable Long idVoucher) {
        Voucher voucher = voucherService.findById(idVoucher);
        if (voucher == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("Voucher not found")
                            .data(null)
                            .build()
            );
        }
        User user = userService.getUserByToken();
        if (user == null) {
            return ResponseEntity.status(401).body(ResponseObject.builder().status(HttpStatus.UNAUTHORIZED.value()).data(null).message("UnAuthorized").build());
        }
        if(user.equals(voucher.getUser())){
            voucher.setUser(null);
        }
        else{
            voucher.setUser(user);
        }

        voucherService.updateVoucher(voucher);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK.value())
                .message("Voucher user updated successfully")
                .data(null)
                .build());
    }
}
