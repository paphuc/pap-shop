package com.pap_shop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Chi_tiet_don_hang")
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class OrderDetai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "don_hang_id")
    private Order donHang;

    @ManyToOne
    @JoinColumn(name = "san_pham_id")
    private Product sanPham;

    private int soLuong;
    private double gia;
}
