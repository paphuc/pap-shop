package com.pap_shop.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Entity representing an item in an order.
 * Contains information about the order item, such as the associated order, product, quantity, and price.
 */
@Entity
@Table(name = "order_items")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    /**
     * The unique identifier for the order item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The order to which this item belongs.
     * The relationship is many-to-one (many order items can belong to one order).
     */
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    private Order order;

    /**
     * The product associated with this order item.
     * The relationship is many-to-one (many order items can be associated with one product).
     */
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * The quantity of the product in the order.
     * This field cannot be null.
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * The price of the product at the time of the order.
     * The value is stored as a BigDecimal with a precision of 10 digits and scale of 2 digits after the decimal point.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}
