package com.pap_shop.service;
import com.pap_shop.entity.Product;
import org.springframework.stereotype.Service;
import com.pap_shop.entity.Category;
import com.pap_shop.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category addCategory(Category category){
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategory(){
        return categoryRepository.findAll();
    }
}
