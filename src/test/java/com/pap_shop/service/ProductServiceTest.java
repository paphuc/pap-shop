//package com.pap_shop.service;
//
//import com.pap_shop.repository.ProductRepository;
//import com.pap_shop.repository.StockEntryRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//class ProductServiceTest {
//
//    @Mock
//    ProductRepository productRepository;
//
//    @Mock
//    StockEntryRepository stockEntryRepository;
//
//    @InjectMocks
//    ProductService productService;
//
//    @Test
//    void deleteProduct_whenProductExists_shouldDeleteSuccessfully() {
//        // Given
//        int productId = 1;
//        when(productRepository.existsById(productId)).thenReturn(true);
//
//        // When
//        productService.deleteProduct(productId);
//
//        // Then
//        verify(stockEntryRepository).deleteByProductId(productId);
//        verify(productRepository).deleteById(productId);
//    }
//
//    @Test
//    void deleteProduct_whenProductDoesNotExist_shouldThrowException() {
//        // Given
//        int productId = 2;
//        when(productRepository.existsById(productId)).thenReturn(false);
//
//        // When & Then
//        RuntimeException exception = assertThrows(RuntimeException.class, () ->
//                productService.deleteProduct(productId)
//        );
//
//        assertEquals("Product not found", exception.getMessage());
//        verify(stockEntryRepository, never()).deleteByProductId(anyInt());
//        verify(productRepository, never()).deleteById(anyInt());
//    }
//}
