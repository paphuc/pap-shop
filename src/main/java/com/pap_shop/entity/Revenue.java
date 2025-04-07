package com.pap_shop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.math.BigDecimal;

/**
 * Entity representing the revenue generated from an order.
 * Contains information about the revenue ID, associated order, total revenue amount, and the date of revenue.
 */
@Entity
@Table(name = "revenue")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Revenue {

    /**
     * The unique identifier for the revenue record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The order associated with this revenue.
     * The relationship is one-to-one (each revenue corresponds to one order).
     */
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * The total revenue generated from the order.
     * The value is stored as a BigDecimal with a precision of 10 digits and scale of 2 digits after the decimal point.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /**
     * The timestamp when the revenue was recorded.
     * This field is not updatable after the revenue record is created.
     */
    @Column(name = "revenue_date", updatable = false)
    private Timestamp revenueDate;
}
