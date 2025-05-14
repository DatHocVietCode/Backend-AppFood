package vn.dk.BackendFoodApp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.dk.BackendFoodApp.dto.ResponseObject;
import vn.dk.BackendFoodApp.dto.response.product.ProductPageResponse;
import vn.dk.BackendFoodApp.dto.response.product.ProductResponse;
import vn.dk.BackendFoodApp.model.Product;
import vn.dk.BackendFoodApp.service.ProductService;

import java.util.ArrayList;
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
//    @Secured("ROLE_USER")
    public ResponseEntity<ResponseObject> getProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false, name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort_by_name,
            @RequestParam(required = false) String sort_by_price
    ) {

        List<Sort.Order> orders = new ArrayList<>();

        if(sort_by_name != null){

            Sort.Direction direction = sort_by_name.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            orders.add(new Sort.Order(direction,"name"));

        }

        if(sort_by_price != null){

            Sort.Direction direction = sort_by_price.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            orders.add(new Sort.Order(direction,"price"));

        }

        if(sort_by_price == null && sort_by_name == null){

            orders.add(new Sort.Order(Sort.Direction.ASC, "id"));
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(orders));

        Page<ProductResponse> productPage = productService.getAllProducts(keyword, categoryId, pageRequest);

        ProductPageResponse response = ProductPageResponse.builder()
                .pageNumber(page)
                .pageSize(size)
                .totalPages(productPage.getTotalPages())
                .totalElements(productPage.getTotalElements())
                .products(productPage.getContent())
                .build();

        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK.value())
                .message("Get products successfully")
                .data(response)
                .build());
    }
    @GetMapping("/best-seller")
    public ResponseEntity<ResponseObject> getProductsBestSeller(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false, name = "category_id") Long categoryId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort_by_name,
            @RequestParam(required = false) String sort_by_price
    ){
        List<ProductResponse> products = productService.getTopSellingProducts(keyword, categoryId, sort_by_name, sort_by_price);



        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK.value())
                .message("Get list of products best seller successfully")
                .data(products)
                .build());
    }
    @GetMapping("/new")
    public ResponseEntity<ResponseObject> getTopNewProduct(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false, name = "category_id") Long categoryId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort_by_name,
            @RequestParam(required = false) String sort_by_price
    ){
        List<ProductResponse> products = productService.getNewProducts(keyword, categoryId, sort_by_name, sort_by_price);

        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK.value())
                .message("Get list of new products successfully")
                .data(products)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getProductById(
            @PathVariable("id") Long productId
    ) throws Exception {
        Product product = productService.getProductById(productId);
        ProductResponse products = ProductResponse.fromProduct(product);
        return ResponseEntity.ok(ResponseObject.builder()
                .data(product)
                .message("Get detail product successfully")
                .status(HttpStatus.OK.value())
                .build());

    }

//    @PostMapping("/like/{productId}")
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
//    public ResponseEntity<ResponseObject> likeProduct(@PathVariable Long productId) throws Exception {
//        User loginUser = securityUtils.getLoggedInUser();
//        Product likedProduct = productService.likeProduct(loginUser.getId(), productId);
//        return ResponseEntity.ok(ResponseObject.builder()
//                .data(ProductResponse.fromProduct(likedProduct))
//                .message("Like product successfully")
//                .status(HttpStatus.OK)
//                .build());
//    }
//    @PostMapping("/unlike/{productId}")
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
//    public ResponseEntity<ResponseObject> unlikeProduct(@PathVariable Long productId) throws Exception {
//        User loginUser = securityUtils.getLoggedInUser();
//        Product unlikedProduct = productService.unlikeProduct(loginUser.getId(), productId);
//        return ResponseEntity.ok(ResponseObject.builder()
//                .data(ProductResponse.fromProduct(unlikedProduct))
//                .message("Unlike product successfully")
//                .status(HttpStatus.OK)
//                .build());
//    }
}
