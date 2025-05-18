package com.pap_shop.entity;

import java.sql.Timestamp;
import java.util.List;
import java.math.BigDecimal;
import lombok.*;

import javax.persistence.*;

/**
 * Entity representing an order placed by a customer.
 * Contains information about order id, customer, total price, status,
 * creation timestamp, and the list of order items.
 */
@Entity
@Table(name = "orders")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    /**
     * The unique identifier for the order.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The customer who placed the order.
     * The relationship is many-to-one (many orders can belong to one customer).
     */
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    /**
     * The total price of the order.
     * The value is stored as a BigDecimal with a precision of 10 digits and scale of 2 digits after the decimal point.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    /**
     * The current status of the order.
     * The status is stored as a string representation of the enum value.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    /**
     * The timestamp when the order was created.
     * This field is not updatable after the order is created.
     */
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    /**
     * The list of items in the order.
     * The relationship is one-to-many (one order can have many order items).
     * CascadeType.ALL ensures that related order items are persisted, updated, or deleted with the order.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
}
