package vn.dk.BackendFoodApp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.dk.BackendFoodApp.model.Product;
import vn.dk.BackendFoodApp.repository.ProductRepository;

import java.awt.print.Pageable;
import java.util.List;

@Service
@Slf4j
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }
    public Page<Product> getBestSeller(int page, int size){

        return null;
    }
}
