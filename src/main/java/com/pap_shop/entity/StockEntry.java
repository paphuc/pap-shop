package com.pap_shop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.math.BigDecimal;

/**
 * Entity representing an entry of stock in the inventory.
 * Contains information about the product, quantity, purchase price, supplier, and the creation timestamp.
 */
@Entity
@Table(name = "stock_entries")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StockEntry {

    /**
     * The unique identifier for the stock entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The product associated with this stock entry.
     * The relationship is many-to-one (many stock entries can relate to one product).
     */
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * The quantity of products in this stock entry.
     * This field cannot be null.
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * The purchase price of the product at the time of entry.
     * The value is stored as a BigDecimal with a precision of 10 digits and scale of 2 digits after the decimal point.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    /**
     * The supplier from whom the product was purchased.
     * This field has a maximum length of 255 characters.
     */
    @Column(length = 255)
    private String supplier;

    /**
     * The timestamp when the stock entry was created.
     * This field is not updatable after the stock entry is created.
     */
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;
}
