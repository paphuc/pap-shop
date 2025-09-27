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

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Cart> getCart(Authentication authentication) {
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        Cart cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@Valid @RequestBody AddToCartRequest request, 
                                         Authentication authentication) {
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        Cart cart = cartService.addToCart(userId, request);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<Cart> updateCartItem(@PathVariable Integer cartItemId,
                                              @Valid @RequestBody UpdateCartItemRequest request,
                                              Authentication authentication) {
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        Cart cart = cartService.updateCartItem(userId, cartItemId, request);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Cart> removeFromCart(@PathVariable Integer cartItemId,
                                              Authentication authentication) {
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        Cart cart = cartService.removeFromCart(userId, cartItemId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCartItemCount(Authentication authentication) {
        String username = authentication.getName();
        Integer userId = userService.findByUsername(username).getId();
        Integer count = cartService.getCartItemCount(userId);
        return ResponseEntity.ok(count);
    }
}