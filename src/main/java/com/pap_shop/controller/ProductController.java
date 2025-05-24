package com.pap_shop.controller;

import com.pap_shop.dto.UpdateProductRequest;
import com.pap_shop.entity.Product;
import com.pap_shop.dto.AddProductRequest;
import com.pap_shop.service.ProductService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing products in the system.
 * Provides endpoints for adding, retrieving, and searching products.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    /**
     * Constructor to inject ProductService.
     *
     * @param productService the service used for product operations
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Adds a new product to the system.
     *
     * @param addProductRequest the data transfer object containing product information
     * @return a ResponseEntity containing the saved product
     */
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody AddProductRequest addProductRequest) {
        Product savedProduct = productService.addProduct(addProductRequest);
        return ResponseEntity.ok(savedProduct);
    }

    /**
     * Retrieves all products from the system.
     *
     * @return a list of all products
     */
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * Retrieves a specific product by its ID.
     *
     * @param ID the ID of the product to retrieve
     * @return a ResponseEntity containing the product if found, or a 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductByID(@PathVariable("id") Integer ID) {
        Optional<Product> product = productService.getProductsByID(ID);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all products in a specific category.
     *
     * @param categoryID the ID of the category to search for
     * @return a ResponseEntity containing a list of products in the specified category
     */
    @GetMapping("/category/{categoryID}")
    public ResponseEntity<List<Product>> getProductByCategoryID(@PathVariable Integer categoryID) {
        List<Product> products = productService.getProductsByCategoryID(categoryID);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer productId,
            @RequestBody UpdateProductRequest request) {
        productService.updateProduct(productId, request);
        return ResponseEntity.ok("Product updated successfully");
    }
}
