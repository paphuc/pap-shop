package com.pap_shop.service;

import com.pap_shop.entity.Category;
import com.pap_shop.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void testAddCategory() {
        // Arrange
        Category category = new Category();
        category.setName("Electronics");

        when(categoryRepository.save(category)).thenReturn(category);

        // Act
        Category result = categoryService.addCategory(category);

        // Assert
        assertEquals("Electronics", result.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void testGetAllCategory() {
        // Arrange
        Category cat1 = new Category();
        cat1.setName("Electronics");
        Category cat2 = new Category();
        cat2.setName("Clothing");

        List<Category> categories = Arrays.asList(cat1, cat2);

        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        List<Category> result = categoryService.getAllCategory();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Electronics", result.get(0).getName());
        assertEquals("Clothing", result.get(1).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testGetCategoryById_whenFound_thenReturnCategory() {
        // Arrange
        Category mockCategory = new Category();
        mockCategory.setID(1);
        mockCategory.setName("Điện tử");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(mockCategory));

        // Act
        Category result = categoryService.getCategoryById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getID());
        assertEquals("Điện tử", result.getName());

        verify(categoryRepository, times(1)).findById(1);
    }

    @Test
    void testGetCategoryById_whenNotFound_thenThrowException() {
        // Arrange
        when(categoryRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            categoryService.getCategoryById(999);
        });

        assertEquals("Category not found with id: 999", ex.getMessage());
        verify(categoryRepository, times(1)).findById(999);
    }
}