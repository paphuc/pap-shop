package com.pap_shop.service;

import com.pap_shop.dto.CreateOrderRequest;
import com.pap_shop.entity.*;
import com.pap_shop.enums.OrderStatus;
import com.pap_shop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

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
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());

            orderItems.add(orderItem);
            totalPrice = totalPrice.add(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        }

        order.setTotalPrice(totalPrice);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteByCartId(cart.getId());

        return savedOrder;
    }

    public List<Order> getUserOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

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

    public Order updateOrderStatus(Integer orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
