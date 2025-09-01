package com.pap_shop.repository;

import com.pap_shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on Product entities.
 * Extends JpaRepository to provide basic CRUD functionality and custom queries.
 */
public interface ProductRepository extends JpaRepository<Product, Integer> {

    /**
     * Finds all products by the specified category ID.
     *
     * @param category the ID of the category to search for products
     * @return a list of products that belong to the specified category
     */
    List<Product> findAllByCategoryID(Integer category);

    /**
     * Finds a product by its SKU.
     *
     * @param sku the SKU of the product to find
     * @return an Optional containing the product if found
     */
    Optional<Product> findBySku(String sku);

    /**
     * Finds all products whose name contains the specified string (case-insensitive).
     *
     * @param name the name or partial name to search for
     * @return a list of products whose names contain the search term
     */
    List<Product> findByNameContainingIgnoreCase(String name);
}
