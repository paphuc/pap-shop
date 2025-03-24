package com.pap_shop.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "khach_hang")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ho_ten", nullable = false)
    private String hoTen;

    @Column(name = "so_dien_thoai", unique = true, nullable = false)
    private String soDienThoai;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "dia_chi")
    private String diaChi;
}
