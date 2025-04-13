package com.pap_shop.repository;
import com.pap_shop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for performing CRUD operations on Category entities.
 * Extends JpaRepository to provide basic CRUD functionality.
 */
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
