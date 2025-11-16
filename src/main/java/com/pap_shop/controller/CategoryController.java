package com.pap_shop.controller;

import com.pap_shop.entity.Category;
import com.pap_shop.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling operations related to Category.
 * Provides endpoints for creating and retrieving categories.
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * Constructor to inject CategoryService.
     *
     * @param categoryService the service used for category operations
     */
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Adds a new category.
     *
     * @param category the category to be added
     * @return the added category
     */
    @PostMapping
    public Category addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    /**
     * Retrieves all categories.
     *
     * @return a list of all categories
     */
    @GetMapping
    public List<Category> getAllCategory() {
        return categoryService.getAllCategory();
    }

    /**
     * Retrieves a specific category by ID.
     *
     * @param id the ID of the category to retrieve
     * @return the category with the specified ID
     */
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Integer id) {
        return categoryService.getCategoryById(id);
    }

    /**
     * Updates an existing category.
     *
     * @param id the ID of the category to update
     * @param category the updated category data
     * @return the updated category
     */
    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Integer id, @RequestBody Category category) {
        return categoryService.updateCategory(id, category);
    }

    /**
     * Deletes a category by ID.
     *
     * @param id the ID of the category to delete
     */
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
    }
}
