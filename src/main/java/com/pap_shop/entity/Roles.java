package com.pap_shop.entity;

import  jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role_list")
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class Roles {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer ID_role;

    @Column(nullable = false, length = 255)
    private String role;
}
