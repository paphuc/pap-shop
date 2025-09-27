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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public Cart getOrCreateCart(Integer userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    Cart cart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(cart);
                });
    }

    public Cart addToCart(Integer userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStock() < request.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());
        
        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + request.getQuantity();
            if (product.getStock() < newQuantity) {
                throw new IllegalArgumentException("Not enough stock available");
            }
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(cartItem);
        }

        return cartRepository.findById(cart.getId()).orElse(cart);
    }

    public Cart updateCartItem(Integer userId, Integer cartItemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to user");
        }

        if (cartItem.getProduct().getStock() < request.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        return cartRepository.findById(cart.getId()).orElse(cart);
    }

    public Cart removeFromCart(Integer userId, Integer cartItemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to user");
        }

        cartItemRepository.delete(cartItem);
        return cartRepository.findById(cart.getId()).orElse(cart);
    }

    public Cart getCart(Integer userId) {
        return getOrCreateCart(userId);
    }

    public void clearCart(Integer userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCartId(cart.getId());
        cartItemRepository.resetAutoIncrement();
    }

    public Integer getCartItemCount(Integer userId) {
        Cart cart = getOrCreateCart(userId);
        return cart.getCartItems().size();
    }
}