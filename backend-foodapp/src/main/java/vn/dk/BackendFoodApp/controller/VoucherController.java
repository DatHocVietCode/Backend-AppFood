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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/vouchers")
public class VoucherController {

    @Autowired
    VoucherService voucherService;
    @Autowired
    UserService userService;

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
