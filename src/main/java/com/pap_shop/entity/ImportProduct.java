package com.pap_shop.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "nhap_hang")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ImportProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "ngay_nhap", nullable = false)
    private Date ngayNhap;

    @Column(name = "nha_cung_cap", nullable = false)
    private String nhaCungCap;

    @Column(name = "san_pham", nullable = false)
    private String sanPham;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "gia_nhap", nullable = false)
    private Double giaNhap;

    @Column(name = "tong_tien", nullable = false)
    private Double tongTien;
}
