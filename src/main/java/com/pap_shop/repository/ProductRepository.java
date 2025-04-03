package com.pap_shop.repository;
import com.pap_shop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import  java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findAllByCategoryID(Integer category);
}