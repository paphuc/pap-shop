package com.pap_shop.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a product in the shop.
 * Contains information about product id, name, category, description, price, stock, and creation timestamp.
 */
@Entity
@Table(name = "products")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Product {

    /**
     * The unique identifier for the product.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The name of the product.
     * This field cannot be null and has a maximum length of 255 characters.
     */
    @Column(nullable = false, length = 255)
    private String name;

    /**
     * The category to which the product belongs.
     * The relationship is many-to-one (many products can belong to one category).
     */
    @ManyToOne
    @JoinColumn(name = "id_category")
    private Category category;

    /**
     * A detailed description of the product.
     * This field is stored as a TEXT column.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * The price of the product.
     * The value is stored as a BigDecimal with a precision of 10 digits and scale of 2 digits after the decimal point.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * The number of items available in stock.
     * This field cannot be null.
     */
    @Column(nullable = false)
    private Integer stock;

    /**
     * The timestamp when the product was created.
     * This field is not updatable after the product is created.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "sku", nullable = false, unique = true, length = 100)
    private String sku;
}
