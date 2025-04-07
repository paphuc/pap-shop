package com.pap_shop.service;

import org.springframework.stereotype.Service;
import com.pap_shop.entity.Category;
import com.pap_shop.repository.CategoryRepository;

import java.util.List;

/**
 * Service class for managing Category entities.
 * Provides business logic for adding and retrieving categories.
 */
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    /**
     * Constructor to inject CategoryRepository.
     *
     * @param categoryRepository the repository used for category operations
     */
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Adds a new category to the system.
     *
     * @param category the category to be added
     * @return the saved category
     */
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * Retrieves all categories from the system.
     *
     * @return a list of all categories
     */
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }
}
