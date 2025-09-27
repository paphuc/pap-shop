package com.pap_shop.controller;

import com.pap_shop.entity.Order;
import com.pap_shop.repository.OrderRepository;
import com.pap_shop.repository.ProductRepository;
import com.pap_shop.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    
    public DashboardController(ProductRepository productRepository, UserRepository userRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalProducts = productRepository.count();

        long totalUsers = userRepository.count();

        long totalOrders = orderRepository.count();

        BigDecimal revenue = orderRepository.getTotalRevenue();
        double totalRevenue = revenue != null ? revenue.doubleValue() : 0.0;
        
        stats.put("totalProducts", totalProducts);
        stats.put("totalUsers", totalUsers);
        stats.put("totalOrders", totalOrders);
        stats.put("revenue", totalRevenue);
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/recent-orders")
    public ResponseEntity<List<Order>> getRecentOrders() {
        List<Order> recentOrders = orderRepository.findRecentOrders();
        List<Order> limitedOrders = recentOrders.size() > 10 ? 
            recentOrders.subList(0, 10) : recentOrders;
        return ResponseEntity.ok(limitedOrders);
    }
}