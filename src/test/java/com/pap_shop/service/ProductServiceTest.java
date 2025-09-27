package com.pap_shop.service;

import com.pap_shop.dto.AddProductRequest;
import com.pap_shop.dto.UpdateProductRequest;
import com.pap_shop.entity.Category;
import com.pap_shop.entity.Product;
import com.pap_shop.exception.ResourceNotFoundException;
import com.pap_shop.repository.CategoryRepository;
import com.pap_shop.repository.ProductRepository;
import com.pap_shop.repository.ProductImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    ProductImageRepository productImageRepository;

    @Mock
    CloudinaryService cloudinaryService;

    @InjectMocks
    ProductService productService;

    private Category testCategory;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setID(1);
        testCategory.setName("Electronics");
        
        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("iPhone 15");
        testProduct.setPrice(new BigDecimal("999.99"));
        testProduct.setStock(10);
        testProduct.setSku("ELE-IPH-1234");
        testProduct.setCategory(testCategory);
    }

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

    @Test
    void addProduct_withValidRequest_shouldReturnSavedProduct() {
        AddProductRequest request = new AddProductRequest();
        request.setName("iPhone 15");
        request.setPrice(new BigDecimal("999.99"));
        request.setCategory(1);
        request.setStock(10);
        request.setDescription("Latest iPhone");
        request.setSku("ELE-IPH-1234");
        
        when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        Product result = productService.addProduct(request);
        
        assertNotNull(result);
        assertEquals("iPhone 15", result.getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void addProduct_withInvalidCategory_shouldThrowException() {
        AddProductRequest request = new AddProductRequest();
        request.setCategory(999);
        
        when(categoryRepository.findById(999)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> productService.addProduct(request));
    }

    @Test
    void getAllProducts_shouldReturnAllProducts() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(products);
        
        List<Product> result = productService.getAllProducts();
        
        assertEquals(1, result.size());
        assertEquals(testProduct, result.get(0));
    }

    @Test
    void getProductsByID_whenExists_shouldReturnProduct() {
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        
        Optional<Product> result = productService.getProductsByID(1);
        
        assertTrue(result.isPresent());
        assertEquals(testProduct, result.get());
    }

    @Test
    void deleteProduct_whenExists_shouldDeleteProduct() {
        when(productRepository.existsById(1)).thenReturn(true);
        
        productService.deleteProduct(1);
        
        verify(productRepository).deleteById(1);
    }

    @Test
    void deleteProduct_whenNotExists_shouldThrowException() {
        when(productRepository.existsById(1)).thenReturn(false);
        
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1));
    }

    @Test
    void updateProductBySku_shouldUpdateProduct() {
        UpdateProductRequest request = new UpdateProductRequest();
        request.setName("iPhone 15 Pro");
        request.setPrice(new BigDecimal("1199.99"));
        
        when(productRepository.findBySku("ELE-IPH-1234")).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        Product result = productService.updateProductBySku("ELE-IPH-1234", request);
        
        assertNotNull(result);
        verify(productRepository).save(testProduct);
    }

    @Test
    void getProductsByCategoryID_shouldReturnProductsInCategory() {
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAllByCategoryID(1)).thenReturn(products);
        
        List<Product> result = productService.getProductsByCategoryID(1);
        
        assertEquals(1, result.size());
        assertEquals(testProduct, result.get(0));
    }
}