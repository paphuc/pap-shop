package com.pap_shop.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import lombok.*;

/**
 * Entity representing a customer.
 * Contains information about customer's id, name, email, phone, address, and creation timestamp.
 */
@Entity
@Table(name = "customers")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

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
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;
}
