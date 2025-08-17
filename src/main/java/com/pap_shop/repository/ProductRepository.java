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
    
    Optional<Product> findBySku(String sku);
}
