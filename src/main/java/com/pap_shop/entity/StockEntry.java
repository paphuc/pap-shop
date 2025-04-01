package com.pap_shop.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.math.BigDecimal;

@Entity
@Table(name = "stock_entries")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StockEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Column(length = 255)
    private String supplier;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;
}
