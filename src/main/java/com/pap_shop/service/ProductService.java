package com.pap_shop.service;

import com.pap_shop.entity.Category;
import com.pap_shop.entity.Product;
import com.pap_shop.dto.AddProductRequest;
import com.pap_shop.repository.CategoryRepository;
import com.pap_shop.repository.ProductRepository;
//import com.pap_shop.repository.StockEntryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Product entities.
 * Provides business logic for adding, retrieving, and searching products.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    CategoryRepository categoryRepository;
//    StockEntryRepository stockEntryRepository;

    /**
     * Adds a new product using product data from a DTO.
     *
     * @param addProductRequest the data transfer object containing product information
     * @return the saved product entity
     * @throws RuntimeException if the category specified in the DTO is not found
     */
    public Product addProduct(AddProductRequest addProductRequest) {
        // Find category by ID and handle case where it is not found
        Category category = categoryRepository.findById(addProductRequest.getCategory())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = new Product();
        product.setName(addProductRequest.getName());
        product.setPrice(addProductRequest.getPrice());
        product.setCategory(category);
        product.setStock(addProductRequest.getStock());
        product.setDescription(addProductRequest.getDescription());

        return productRepository.save(product);
    }

    /**
     * Adds a new product using the provided product entity.
     *
     * @param product the product entity to be added
     * @return the saved product entity
     */
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Retrieves all products from the system.
     *
     * @return a list of all products
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param ID the ID of the product to retrieve
     * @return an Optional containing the product if found, or an empty Optional if not found
     */
    public Optional<Product> getProductsByID(Integer ID) {
        return productRepository.findById(ID);
    }

    /**
     * Retrieves all products that belong to a specific category.
     *
     * @param ID the ID of the category to search for
     * @return a list of products belonging to the specified category
     */
    public List<Product> getProductsByCategoryID(Integer ID) {
        return productRepository.findAllByCategoryID(ID);
    }
//
//    public void deleteProduct(Integer productId) {
//        if (!productRepository.existsById(productId)) {
//            throw new RuntimeException("Product not found");
//        }
//        stockEntryRepository.deleteByProductId(productId);
//        productRepository.deleteById(productId);
//    }

    public void exportProduct(Integer productID, Integer quantity){
        Product product = productRepository.findById(productID)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if(product.getStock() < quantity){
            throw  new RuntimeException("Not enough stock to export");
        }
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}
