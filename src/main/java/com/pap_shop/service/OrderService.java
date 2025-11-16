package com.pap_shop.service;

import com.pap_shop.dto.CreateOrderRequest;
import com.pap_shop.entity.*;
import com.pap_shop.enums.OrderStatus;
import com.pap_shop.exception.InsufficientStockException;
import com.pap_shop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing orders
 * Handles order creation, retrieval, status updates, and stock management
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    /**
     * Create a new order from user's cart items
     * Validates stock availability and updates product stock
     * Clears cart after successful order creation
     * 
     * @param username User's username
     * @param request Order details including shipping address and notes
     * @return Created order
     * @throws RuntimeException if user not found, cart not found, or cart is empty
     * @throws InsufficientStockException if any product has insufficient stock
     */
    @Transactional
    public Order createOrder(String username, CreateOrderRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        order.setShippingAddress(request.getShippingAddress());
        order.setNotes(request.getNotes());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            
            if (product.getStock() < cartItem.getQuantity()) {
                throw new InsufficientStockException("Sản phẩm " + product.getName() + " không đủ hàng");
            }
            
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());

            orderItems.add(orderItem);
            totalPrice = totalPrice.add(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        }

        order.setTotalPrice(totalPrice);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteByCartId(cart.getId());

        return savedOrder;
    }

    /**
     * Get all orders for a specific user
     * 
     * @param username User's username
     * @return List of user's orders
     * @throws RuntimeException if user not found
     */
    public List<Order> getUserOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Get specific order by ID for authenticated user
     * Validates that the order belongs to the requesting user
     * 
     * @param orderId Order ID
     * @param username User's username
     * @return Order details
     * @throws RuntimeException if order not found, user not found, or unauthorized access
     */
    public Order getOrderById(Integer orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to order");
        }

        return order;
    }

    /**
     * Cancel order (User can cancel their own order)
     * Only PENDING orders can be canceled
     * Restores product stock when canceled
     * 
     * @param orderId Order ID
     * @param username User's username
     * @return Updated order
     * @throws RuntimeException if order not found, unauthorized, or cannot be canceled
     */
    @Transactional
    public Order cancelOrder(Integer orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to order");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Only pending orders can be canceled");
        }

        // Restore stock
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            product.setStock(product.getStock() + orderItem.getQuantity());
            productRepository.save(product);
        }
        
        order.setStatus(OrderStatus.CANCELED);
        return orderRepository.save(order);
    }

    /**
     * Update order status (Admin only)
     * If status is changed to CANCELED, restores product stock
     * 
     * @param orderId Order ID
     * @param status New order status
     * @return Updated order
     * @throws RuntimeException if order not found
     */
    @Transactional
    public Order updateOrderStatus(Integer orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (status == OrderStatus.CANCELED && order.getStatus() != OrderStatus.CANCELED) {
            for (OrderItem orderItem : order.getOrderItems()) {
                Product product = orderItem.getProduct();
                product.setStock(product.getStock() + orderItem.getQuantity());
                productRepository.save(product);
            }
        }
        
        order.setStatus(status);
        return orderRepository.save(order);
    }

    /**
     * Get all orders in the system (Admin only)
     * 
     * @return List of all orders
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
