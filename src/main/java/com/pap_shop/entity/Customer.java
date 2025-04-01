package com.pap_shop.entity;
import jakarta.persistence.*;
import java.sql.Timestamp;
import lombok.*;

@Entity
@Table(name = "customers")
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 15)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;
}
