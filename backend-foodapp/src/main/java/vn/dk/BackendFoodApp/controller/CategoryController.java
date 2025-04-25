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
import vn.dk.BackendFoodApp.model.Category;
import vn.dk.BackendFoodApp.payload.ResponseObject;
import vn.dk.BackendFoodApp.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Tag(name = "Category Controller")
@Slf4j(topic = "CATEGORY-CONTROLLER")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllCategories(){

        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get list of categories successfully")
                .status(HttpStatus.OK.value())
                .data(categories)
                .build());
    }

}
