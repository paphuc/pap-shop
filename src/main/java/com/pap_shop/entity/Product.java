package com.pap_shop.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "san_pham")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ten_san_pham", nullable = false)
    private String tenSanPham;

    @Column(name = "loai", nullable = false)
    private String loai;

    @Column(name = "gia_nhap", nullable = false)
    private Double giaNhap;

    @Column(name = "gia_ban", nullable = false)
    private Double giaBan;

    @Column(name = "so_luong_ton")
    private Integer soLuongTon;

    @Column(name = "nha_cung_cap")
    private String nhaCungCap;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL)
    private List<OrderDetai> chiTietDonHangs;


    @Temporal(TemporalType.DATE)
    @Column(name = "ngay_cap_nhat")
    private Date ngayCapNhat;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImportProduct> nhapHangList;
}
