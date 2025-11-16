package com.pap_shop.controller;

import com.pap_shop.dto.CreateOrderRequest;
import com.pap_shop.entity.Order;
import com.pap_shop.enums.OrderStatus;
import com.pap_shop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for managing orders
 * Handles order creation, retrieval, and status updates
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Order management API")
public class OrderController {

    private final OrderService orderService;

    /**
     * Create a new order for the authenticated user
     * @param request Order details including items and shipping info
     * @param authentication User authentication info
     * @return Created order
     */
    @PostMapping
    @Operation(summary = "Create new order", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<Order> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String username = jwt.getSubject();
        
        Order order = orderService.createOrder(username, request);
        return ResponseEntity.ok(order);
    }

    /**
     * Get all orders of the authenticated user
     * @param authentication User authentication info
     * @return List of user's orders
     */
    @GetMapping
    @Operation(summary = "Get user orders", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<List<Order>> getUserOrders(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String username = jwt.getSubject();
        
        List<Order> orders = orderService.getUserOrders(username);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get specific order by ID for the authenticated user
     * @param orderId Order ID
     * @param authentication User authentication info
     * @return Order details
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<Order> getOrder(
            @PathVariable Integer orderId,
            Authentication authentication) {
        
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String username = jwt.getSubject();
        
        Order order = orderService.getOrderById(orderId, username);
        return ResponseEntity.ok(order);
    }

    /**
     * Cancel order (User can cancel their own order)
     * @param orderId Order ID
     * @param authentication User authentication info
     * @return Updated order
     */
    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<Order> cancelOrder(
            @PathVariable Integer orderId,
            Authentication authentication) {
        
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String username = jwt.getSubject();
        
        Order order = orderService.cancelOrder(orderId, username);
        return ResponseEntity.ok(order);
    }

    /**
     * Update order status (Admin only)
     * @param orderId Order ID
     * @param status New order status
     * @return Updated order
     */
    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status (Admin)", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestParam OrderStatus status) {
        
        Order order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(order);
    }

    /**
     * Get all orders in the system (Admin only)
     * @return List of all orders
     */
    @GetMapping("/admin/all")
    @Operation(summary = "Get all orders (Admin)", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
}
