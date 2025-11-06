package com.pap_shop.controller;

import com.pap_shop.dto.CreateOrderRequest;
import com.pap_shop.entity.Order;
import com.pap_shop.enums.OrderStatus;
import com.pap_shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String username = jwt.getSubject();
        
        Order order = orderService.createOrder(username, request);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String username = jwt.getSubject();
        
        List<Order> orders = orderService.getUserOrders(username);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(
            @PathVariable Integer orderId,
            Authentication authentication) {
        
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String username = jwt.getSubject();
        
        Order order = orderService.getOrderById(orderId, username);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestParam OrderStatus status) {
        
        Order order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
}
