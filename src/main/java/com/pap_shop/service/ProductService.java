package com.pap_shop.service;

import com.pap_shop.entity.Category;
import com.pap_shop.entity.Product;
import com.pap_shop.model.ProductDTO;
import com.pap_shop.repository.CategoryRepository;
import com.pap_shop.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product addProduct(ProductDTO productDTO) {
        Category category = categoryRepository.findById(productDTO.getCategory())
                .orElseThrow(() -> new RuntimeException("Category not found"));


        Product product = new Product();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setCategory(category);
        product.setStock(productDTO.getStock());
        product.setDescription(product.getDescription());

        return productRepository.save(product);
    }
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

//    public void delALlProducts(){
//        productRepository.deleteAll();
//    }

    public Optional<Product> getProductsByID(Integer ID){
        return productRepository.findById(ID);
    }

    public List<Product> getProductsByCatogoryID(Integer ID){
        return productRepository.findAllByCategoryID(ID);
    }
}
