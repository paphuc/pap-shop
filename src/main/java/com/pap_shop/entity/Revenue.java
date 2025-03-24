package com.pap_shop.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "doanh_thu")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Revenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "ngay", nullable = false)
    private Date ngay;

    @Column(name = "ma_don_hang", nullable = false)
    private String maDonHang;

    @Column(name = "khach_hang", nullable = false)
    private String khachHang;

    @Column(name = "tong_tien", nullable = false)
    private Double tongTien;

    @Column(name = "phuong_thuc_thanh_toan", nullable = false)
    private String phuongThucThanhToan;
}
