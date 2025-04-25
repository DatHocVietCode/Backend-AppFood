package vn.dk.BackendFoodApp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.dk.BackendFoodApp.model.Product;
import vn.dk.BackendFoodApp.payload.ResponseObject;
import vn.dk.BackendFoodApp.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "Product Controller")
@Slf4j(topic = "PRODUCT-CONTROLLER")
@RequiredArgsConstructor
@Validated
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllProducts(){

        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get list of categories successfully")
                .status(HttpStatus.OK.value())
                .data(products)
                .build());
    }


    @GetMapping("/best-seller")
    public ResponseEntity<ResponseObject> getProductBestSeller(){

        List<Product> products
    }
}
