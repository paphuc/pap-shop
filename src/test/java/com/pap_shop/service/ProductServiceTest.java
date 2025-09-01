package com.pap_shop.service;

import com.pap_shop.entity.Product;
import com.pap_shop.repository.CategoryRepository;
import com.pap_shop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    ProductService productService;

    @Test
    void searchProductsByName_whenProductsFound_shouldReturnMatchingProducts() {
        // Given
        String searchTerm = "iphone";
        Product product1 = new Product();
        product1.setName("iPhone 15");
        Product product2 = new Product();
        product2.setName("iPhone 14 Pro");
        List<Product> expectedProducts = Arrays.asList(product1, product2);
        
        when(productRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(expectedProducts);

        // When
        List<Product> result = productService.searchProductsByName(searchTerm);

        // Then
        assertEquals(2, result.size());
        assertEquals(expectedProducts, result);
        verify(productRepository).findByNameContainingIgnoreCase(searchTerm);
    }

    @Test
    void searchProductsByName_whenNoProductsFound_shouldReturnEmptyList() {
        // Given
        String searchTerm = "nonexistent";
        when(productRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(Collections.emptyList());

        // When
        List<Product> result = productService.searchProductsByName(searchTerm);

        // Then
        assertTrue(result.isEmpty());
        verify(productRepository).findByNameContainingIgnoreCase(searchTerm);
    }
}