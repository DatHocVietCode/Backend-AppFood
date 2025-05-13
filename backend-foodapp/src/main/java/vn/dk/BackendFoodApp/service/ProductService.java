package vn.dk.BackendFoodApp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.dk.BackendFoodApp.model.Product;
import vn.dk.BackendFoodApp.dto.response.product.ProductResponse;
import vn.dk.BackendFoodApp.repository.ProductRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public Page<ProductResponse> getAllProducts(String keyword,
                                            Long categoryId, PageRequest pageRequest) {
        // Lấy danh sách sản phẩm theo trang (page), giới hạn (limit), và categoryId (nếu có)
        Page<Product> productsPage;
        productsPage = productRepository.searchProducts(categoryId, keyword, pageRequest);
        return productsPage.map(ProductResponse::fromProduct);
    }
    public List<ProductResponse> getTopSellingProducts(String keyword,
                                                       Long categoryId,
                                                       String sortByName,
                                                       String sortByPrice) {

        List<ProductResponse> products =  productRepository.findTop10ByOrderBySoldDesc().stream().map(ProductResponse::fromProduct).toList();
        // Filter theo category
        if (categoryId != null) {
            products = products.stream()
                    .filter(p -> categoryId.equals(p.getCategoryId()))
                    .collect(Collectors.toList());
        }

        // Filter theo keyword (tìm trong name và description)
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.trim().toLowerCase();
            products = products.stream()
                    .filter(p ->
                            (p.getName() != null && p.getName().toLowerCase().contains(lowerKeyword)) ||
                                    (p.getDescription() != null && p.getDescription().toLowerCase().contains(lowerKeyword))
                    )
                    .collect(Collectors.toList());
        }
        // Sort theo giá
        if ("asc".equalsIgnoreCase(sortByPrice)) {
            products.sort(Comparator.comparing(ProductResponse::getPrice));
        } else if ("desc".equalsIgnoreCase(sortByPrice)) {
            products.sort(Comparator.comparing(ProductResponse::getPrice).reversed());
        }

        // Sort theo tên
        if ("asc".equalsIgnoreCase(sortByName)) {
            products.sort(Comparator.comparing(ProductResponse::getName, String.CASE_INSENSITIVE_ORDER));
        } else if ("desc".equalsIgnoreCase(sortByName)) {
            products.sort(Comparator.comparing(ProductResponse::getName, String.CASE_INSENSITIVE_ORDER).reversed());
        }

        return products;
    }
    public List<ProductResponse> getNewProducts(String keyword,
                                                       Long categoryId,
                                                       String sortByName,
                                                       String sortByPrice) {

        List<ProductResponse> products =  productRepository.findTop10ByOrderByCreatedAtDesc().stream().map(ProductResponse::fromProduct).toList();
        // Filter theo category
        if (categoryId != null) {
            products = products.stream()
                    .filter(p -> categoryId.equals(p.getCategoryId()))
                    .collect(Collectors.toList());
        }

        // Filter theo keyword (tìm trong name và description)
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.trim().toLowerCase();
            products = products.stream()
                    .filter(p ->
                            (p.getName() != null && p.getName().toLowerCase().contains(lowerKeyword)) ||
                                    (p.getDescription() != null && p.getDescription().toLowerCase().contains(lowerKeyword))
                    )
                    .collect(Collectors.toList());
        }
        // Sort theo giá
        if ("asc".equalsIgnoreCase(sortByPrice)) {
            products.sort(Comparator.comparing(ProductResponse::getPrice));
        } else if ("desc".equalsIgnoreCase(sortByPrice)) {
            products.sort(Comparator.comparing(ProductResponse::getPrice).reversed());
        }

        // Sort theo tên
        if ("asc".equalsIgnoreCase(sortByName)) {
            products.sort(Comparator.comparing(ProductResponse::getName, String.CASE_INSENSITIVE_ORDER));
        } else if ("desc".equalsIgnoreCase(sortByName)) {
            products.sort(Comparator.comparing(ProductResponse::getName, String.CASE_INSENSITIVE_ORDER).reversed());
        }

        return products;
    }

    public ProductResponse getProductById(long productId){
        ProductResponse product = productRepository.findById(productId)
                .stream()
                .map(ProductResponse::fromProduct)
                .findFirst()
                .orElse(null);
        return product;
    }
}
