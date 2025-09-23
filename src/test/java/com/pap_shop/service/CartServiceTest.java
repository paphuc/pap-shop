package com.pap_shop.service;

import com.pap_shop.dto.AddToCartRequest;
import com.pap_shop.dto.UpdateCartItemRequest;
import com.pap_shop.entity.Cart;
import com.pap_shop.entity.CartItem;
import com.pap_shop.entity.Product;
import com.pap_shop.entity.User;
import com.pap_shop.exception.ResourceNotFoundException;
import com.pap_shop.repository.CartItemRepository;
import com.pap_shop.repository.CartRepository;
import com.pap_shop.repository.ProductRepository;
import com.pap_shop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Product testProduct;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        
        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setStock(10);
        
        testCart = new Cart();
        testCart.setId(1);
        testCart.setUser(testUser);
    }

    @Test
    void getOrCreateCart_existingCart_returnsCart() {
        when(cartRepository.findByUserId(1)).thenReturn(Optional.of(testCart));
        
        Cart result = cartService.getOrCreateCart(1);
        
        assertEquals(testCart, result);
        verify(cartRepository).findByUserId(1);
    }

    @Test
    void getOrCreateCart_newCart_createsAndReturnsCart() {
        when(cartRepository.findByUserId(1)).thenReturn(Optional.empty());
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        
        Cart result = cartService.getOrCreateCart(1);
        
        assertEquals(testCart, result);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addToCart_success() {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(1);
        request.setQuantity(2);
        
        when(cartRepository.findByUserId(1)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.findByCartIdAndProductId(1, 1)).thenReturn(Optional.empty());
        when(cartRepository.findById(1)).thenReturn(Optional.of(testCart));
        
        Cart result = cartService.addToCart(1, request);
        
        assertEquals(testCart, result);
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void addToCart_insufficientStock_throwsException() {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(1);
        request.setQuantity(15);
        
        when(cartRepository.findByUserId(1)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        
        assertThrows(IllegalArgumentException.class, () -> cartService.addToCart(1, request));
    }

    @Test
    void removeFromCart_success() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1);
        cartItem.setCart(testCart);
        
        when(cartRepository.findByUserId(1)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findById(1)).thenReturn(Optional.of(cartItem));
        when(cartRepository.findById(1)).thenReturn(Optional.of(testCart));
        
        Cart result = cartService.removeFromCart(1, 1);
        
        assertEquals(testCart, result);
        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    void clearCart_success() {
        when(cartRepository.findByUserId(1)).thenReturn(Optional.of(testCart));
        
        cartService.clearCart(1);
        
        verify(cartItemRepository).deleteByCartId(1);
        verify(cartItemRepository).resetAutoIncrement();
    }
}