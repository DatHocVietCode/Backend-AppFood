package vn.dk.BackendFoodApp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.dk.BackendFoodApp.model.Category;
import vn.dk.BackendFoodApp.repository.CategoryRepository;

import java.util.List;

@Service
@Slf4j
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }
}
