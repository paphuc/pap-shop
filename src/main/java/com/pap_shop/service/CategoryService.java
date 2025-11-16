package com.pap_shop.service;

import com.pap_shop.exception.ResourceNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import com.pap_shop.entity.Category;
import com.pap_shop.repository.CategoryRepository;

import java.util.List;

/**
 * Service class for managing Category entities.
 * Provides business logic for adding and retrieving categories.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {

    CategoryRepository categoryRepository;

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

    /**
     * Retrieves a category by its ID.
     *
     * @param id the ID of the category to retrieve
     * @return the category with the specified ID
     * @throws ResourceNotFoundException if category is not found
     */
    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    /**
     * Updates an existing category.
     *
     * @param id the ID of the category to update
     * @param category the updated category data
     * @return the updated category
     * @throws ResourceNotFoundException if category is not found
     */
    public Category updateCategory(Integer id, Category category) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        
        existingCategory.setName(category.getName());
        return categoryRepository.save(existingCategory);
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id the ID of the category to delete
     * @throws ResourceNotFoundException if category is not found
     */
    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
