package vn.dk.BackendFoodApp.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.dk.BackendFoodApp.dto.ResponseObject;
import vn.dk.BackendFoodApp.dto.request.address.AddressRequest;
import vn.dk.BackendFoodApp.dto.response.address.AddressResponse;
import vn.dk.BackendFoodApp.service.AddressService;
import vn.dk.BackendFoodApp.service.TokenService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping("/add")
    public ResponseEntity<ResponseObject> addAddress(@RequestBody @Valid AddressRequest request) {
        Optional<String> usernameOpt = TokenService.getCurrentUserLogin();

        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseObject.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("Unauthorized")
                            .data(null)
                            .build()
            );
        }

        try {
            addressService.addAddress(request, usernameOpt.get());

            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK.value())
                            .message("Address added successfully.")
                            .data(null)
                            .build()
            );

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }
    @GetMapping("/my-address")
    public ResponseEntity<ResponseObject> getMyAddresses() {
        Optional<String> usernameOpt = TokenService.getCurrentUserLogin();

        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseObject.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("Unauthorized")
                            .data(null)
                            .build()
            );
        }

        List<AddressResponse> addresses = addressService.getAddressesByUsername(usernameOpt.get());

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK.value())
                        .message("Addresses retrieved successfully.")
                        .data(addresses)
                        .build()
        );
    }
    @DeleteMapping("/delete/{idAddress}")
    public ResponseEntity<ResponseObject> deleteAddress(@PathVariable Long idAddress) {
        Optional<String> usernameOpt = TokenService.getCurrentUserLogin();

        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseObject.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("Unauthorized")
                            .data(null)
                            .build()
            );
        }

        try {
            addressService.deleteAddress(idAddress, usernameOpt.get());

            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(HttpStatus.OK.value())
                            .message("Address deleted successfully.")
                            .data(null)
                            .build()
            );

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        }
    }
    @GetMapping("/default")
    public ResponseEntity<ResponseObject> getDefaultAddress() {
        Optional<String> usernameOpt = TokenService.getCurrentUserLogin();

        if (usernameOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseObject.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("Unauthorized")
                            .data(null)
                            .build()
            );
        }

        AddressResponse defaultAddress = addressService.getDefaultAddressByUsername(usernameOpt.get());

        if (defaultAddress == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseObject.builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message("No default address found")
                            .data(null)
                            .build()
            );
        }

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK.value())
                        .message("Default address retrieved successfully.")
                        .data(defaultAddress)
                        .build()
        );
    }

}
