package com.pap_shop.repository;

import com.pap_shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on Customer entities.
 * Extends JpaRepository to provide basic CRUD functionality.
 */
public interface UserRepository extends  JpaRepository<User, Integer>{
    /**
     * Finds all products by the specified category ID.
     *
     * @param email the email of the customer to search for user
     * @return the specified customer
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds all products by the specified category ID.
     *
     * @param phone the phone of the customer to search for user
     * @return the specified customer
     */
    Optional<User> findByPhone(String phone);
}
