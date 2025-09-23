package com.pap_shop.controller;

import com.pap_shop.dto.AddToCartRequest;
import com.pap_shop.dto.UpdateCartItemRequest;
import com.pap_shop.entity.Cart;
import com.pap_shop.service.CartService;
import com.pap_shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST controller for managing shopping cart operations.
 * Provides endpoints for cart management including adding, updating, and removing items.
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    /**
     * Get user's shopping cart.
     *
     * @param authentication the authenticated user
     * @return the user's cart
     */
    @GetMapping
    public ResponseEntity<Cart> getCart(Authentication authentication) {
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        Cart cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    /**
     * Add item to cart.
     *
     * @param request the add to cart request containing product and quantity
     * @param authentication the authenticated user
     * @return the updated cart
     */
    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@Valid @RequestBody AddToCartRequest request, 
                                         Authentication authentication) {
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        Cart cart = cartService.addToCart(userId, request);
        return ResponseEntity.ok(cart);
    }

    /**
     * Update cart item quantity.
     *
     * @param cartItemId the cart item ID to update
     * @param request the update request containing new quantity
     * @param authentication the authenticated user
     * @return the updated cart
     */
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<Cart> updateCartItem(@PathVariable Integer cartItemId,
                                              @Valid @RequestBody UpdateCartItemRequest request,
                                              Authentication authentication) {
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        Cart cart = cartService.updateCartItem(userId, cartItemId, request);
        return ResponseEntity.ok(cart);
    }

    /**
     * Remove item from cart.
     *
     * @param cartItemId the cart item ID to remove
     * @param authentication the authenticated user
     * @return the updated cart
     */
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Cart> removeFromCart(@PathVariable Integer cartItemId,
                                              Authentication authentication) {
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        Cart cart = cartService.removeFromCart(userId, cartItemId);
        return ResponseEntity.ok(cart);
    }

    /**
     * Clear all items from cart.
     *
     * @param authentication the authenticated user
     * @return empty response
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get total number of items in cart.
     *
     * @param authentication the authenticated user
     * @return the total item count
     */
    @GetMapping("/count")
    public ResponseEntity<Integer> getCartItemCount(Authentication authentication) {
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        Integer count = cartService.getCartItemCount(userId);
        return ResponseEntity.ok(count);
    }
}