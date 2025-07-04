package com.pap_shop.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;

/**
 * Entity representing a customer.
 * Contains information about customer's id, name, email, phone, address, and creation timestamp.
 */
@Entity
@Table(name = "users")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    /**
     * The unique identifier for the customer.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The name of the customer.
     * Cannot be null and has a maximum length of 100 characters.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * The email of the customer.
     * Cannot be null, must be unique, and has a maximum length of 100 characters.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * The phone number of the customer.
     * Cannot be null, must be unique, and has a maximum length of 15 characters.
     */
    @Column(nullable = false, unique = true, length = 15)
    private String phone;

    /**
     * The address of the customer.
     * Can be null and stored as a TEXT column.
     */
    @Column(columnDefinition = "TEXT")
    private String address;

    /**
     * The timestamp when the customer record was created.
     * This field is not updatable.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    /**
     * The password of customer's account
     */
    @Column(name = "password",nullable = false)
    private String password;

    @Column(name = "username",nullable = false)
    private  String username;

    @ManyToOne
    @JoinColumn(name = "id_role",nullable = false)
    private Roles role;

    @Column(name = "update_at")
    private LocalDateTime updateAt;
}
