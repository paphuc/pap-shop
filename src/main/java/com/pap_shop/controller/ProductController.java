package com.pap_shop.controller;

import com.pap_shop.entity.Product;
import com.pap_shop.entity.ProductImage;
import com.pap_shop.dto.AddProductRequest;
import com.pap_shop.dto.UpdateProductRequest;
import com.pap_shop.dto.AddImageRequest;
import com.pap_shop.service.ProductService;
import com.pap_shop.service.ProductImportService;
import com.pap_shop.util.ProductExcelExporter;
import com.pap_shop.util.ProductExcelTemplate;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
    private final ProductImportService productImportService;

    /**
     * Constructor to inject ProductService.
     *
     * @param productService the service used for product operations
     */
    public ProductController(ProductService productService, ProductImportService productImportService) {
        this.productService = productService;
        this.productImportService = productImportService;
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


    /**
     * Export all products to Excel file.
     *
     * @return Excel file containing all products
     * @throws Exception if export fails
     */
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

    /**
     * Updates a product by its SKU.
     *
     * @param sku the SKU of the product to update
     * @param updateRequest the data transfer object containing updated product information
     * @return a ResponseEntity containing the updated product
     */
    @PutMapping("/update/{sku}")
    public ResponseEntity<Product> updateProductBySku(@PathVariable String sku, @RequestBody UpdateProductRequest updateRequest) {
        Product updatedProduct = productService.updateProductBySku(sku, updateRequest);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Import products from Excel file.
     *
     * @param file the Excel file containing products to import
     * @return success message with import count
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importExcel(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {
            List<Product> importedProducts = productImportService.importProductsFromExcel(file);
            return ResponseEntity.ok("Imported " + importedProducts.size() + " products successfully");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error reading Excel file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error importing products: " + e.getMessage());
        }
    }

    /**
     * Download Excel template for product import.
     *
     * @return Excel template file
     * @throws IOException if template generation fails
     */
    @GetMapping("/template")
    public ResponseEntity<InputStreamResource> downloadTemplate() throws IOException {
        ByteArrayInputStream template = ProductExcelTemplate.generateTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=product-import-template.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(template));
    }

    /**
     * Searches for products by name (case-insensitive partial match).
     *
     * @param name the name or partial name to search for
     * @return a ResponseEntity containing a list of products matching the search term
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductsByName(@RequestParam String name) {
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    /**
     * Add image to product.
     *
     * @param productId the product ID
     * @param request the image request containing image URL
     * @return the added product image
     */
    @PostMapping("/{productId}/images")
    public ResponseEntity<ProductImage> addImageToProduct(@PathVariable Integer productId, @RequestBody AddImageRequest request) {
        ProductImage image = productService.addImageToProduct(productId, request);
        return ResponseEntity.ok(image);
    }

    /**
     * Delete product image.
     *
     * @param imageId the image ID to delete
     * @return success message
     */
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Integer imageId) {
        productService.deleteImage(imageId);
        return ResponseEntity.ok("Image deleted successfully");
    }

    /**
     * Upload image file to product.
     *
     * @param productId the product ID
     * @param file the image file to upload
     * @return the uploaded product image
     */
    @PostMapping(value = "/{productId}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductImage> uploadImageToProduct(@PathVariable Integer productId, @RequestParam("file") MultipartFile file) {
        ProductImage image = productService.uploadImageToProduct(productId, file);
        return ResponseEntity.ok(image);
    }

    /**
     * Get all images for a product.
     *
     * @param productId the product ID
     * @return list of product images
     */
    @GetMapping("/{productId}/images")
    public ResponseEntity<List<ProductImage>> getProductImages(@PathVariable Integer productId) {
        List<ProductImage> images = productService.getProductImages(productId);
        return ResponseEntity.ok(images);
    }
}
