package com.pap_shop.service;

import com.pap_shop.dto.UpdateProductRequest;
import com.pap_shop.entity.Category;
import com.pap_shop.entity.Product;
import com.pap_shop.repository.CategoryRepository;
import com.pap_shop.repository.ProductRepository;
import com.pap_shop.repository.StockEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private StockEntryRepository stockEntryRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateProduct_Successfully_WhenProductAndCategoryExist() {
        // Given
        Integer productId = 1;
        Integer categoryId = 10;

        Product product = new Product();
        product.setId(productId);

        Category category = new Category();
        category.setID(categoryId);
        category.setName("Updated Category");

        UpdateProductRequest request = new UpdateProductRequest();
        request.setName("New Name");
        request.setDescription("New Description");
        request.setPrice(BigDecimal.valueOf(150.00));
        request.setStock(50);
        request.setCategoryId(categoryId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // When
        productService.updateProduct(productId, request);

        // Then
        assertEquals("New Name", product.getName());
        assertEquals("New Description", product.getDescription());
        assertEquals(BigDecimal.valueOf(150.00), product.getPrice());
        assertEquals(50, product.getStock());
        assertEquals(category, product.getCategory());

        verify(productRepository).save(product);
    }

    @Test
    void updateProduct_ShouldThrowException_WhenProductNotFound() {
        // Given
        Integer productId = 999;
        UpdateProductRequest request = new UpdateProductRequest();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(productId, request);
        });

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_ShouldThrowException_WhenCategoryNotFound() {
        // Given
        Integer productId = 1;
        Integer invalidCategoryId = 999;

        Product product = new Product();
        product.setId(productId);

        UpdateProductRequest request = new UpdateProductRequest();
        request.setCategoryId(invalidCategoryId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(invalidCategoryId)).thenReturn(Optional.empty());

        // Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(productId, request);
        });

        assertEquals("Category not found", exception.getMessage());
        verify(productRepository, never()).save(any());
    }
}
