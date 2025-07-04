package com.pap_shop.controller;

import com.pap_shop.entity.Product;
import com.pap_shop.dto.AddProductRequest;
import com.pap_shop.service.ProductService;
import com.pap_shop.util.ProductExcelExporter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
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

    /**
     * Deletes a product by its ID.
     *
     * @param productId the ID of the product to delete
     * @return a ResponseEntity containing a success message if the product is deleted successfully
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok("Product deleted successfully");
    }


    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportExcel() throws Exception {
        List<Product> products = productService.getAllProducts();
        ByteArrayInputStream in = ProductExcelExporter.export(products);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=products.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}
