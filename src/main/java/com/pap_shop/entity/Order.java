package com.pap_shop.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "don_hang")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "ngay_dat", nullable = false)
    private Date ngayDat;

    @ManyToOne
    @JoinColumn(name = "khach_hang_id", nullable = false)
    private Customer khachHang;

    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL)
    private List<OrderDetai> chiTietDonHangs;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "tong_tien", nullable = false)
    private Double tongTien;

    @Column(name = "trang_thai", nullable = false)
    private String trangThai;
}
