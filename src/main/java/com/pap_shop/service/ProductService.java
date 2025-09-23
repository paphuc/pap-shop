package com.pap_shop.service;

import com.pap_shop.entity.Category;
import com.pap_shop.entity.Product;
import com.pap_shop.entity.ProductImage;
import com.pap_shop.dto.AddProductRequest;
import com.pap_shop.dto.UpdateProductRequest;
import com.pap_shop.dto.AddImageRequest;
import com.pap_shop.exception.ResourceNotFoundException;
import com.pap_shop.repository.CategoryRepository;
import com.pap_shop.repository.ProductRepository;
import com.pap_shop.repository.ProductImageRepository;
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
    ProductImageRepository productImageRepository;
    CloudinaryService cloudinaryService;

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
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = new Product();
        product.setName(addProductRequest.getName());
        product.setPrice(addProductRequest.getPrice());
        product.setCategory(category);
        product.setStock(addProductRequest.getStock());
        product.setDescription(addProductRequest.getDescription());

        String sku = addProductRequest.getSku();
        if (sku == null || sku.trim().isEmpty()) {
            sku = generateSku(addProductRequest.getName(), category.getName());
        }
        product.setSku(sku);
        
        return productRepository.save(product);
    }
    
    /**
     * Generate SKU for product based on category and product name.
     *
     * @param productName the product name
     * @param categoryName the category name
     * @return generated SKU
     */
    private String generateSku(String productName, String categoryName) {
        String prefix = categoryName.substring(0, Math.min(3, categoryName.length())).toUpperCase();
        String namePart = productName.replaceAll("\\s+", "").substring(0, Math.min(3, productName.replaceAll("\\s+", "").length())).toUpperCase();
        long timestamp = System.currentTimeMillis() % 10000;
        return prefix + "-" + namePart + "-" + timestamp;
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

    /**
     * Deletes a product by its ID.
     *
     * @param productId the ID of the product to delete
     * @throws ResourceNotFoundException if product is not found
     */
    public void deleteProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productRepository.deleteById(productId);
    }

    /**
     * Updates a product by its SKU.
     *
     * @param sku the SKU of the product to update
     * @param updateRequest the data transfer object containing updated product information
     * @return the updated product entity
     * @throws RuntimeException if the product or category is not found
     */
    public Product updateProductBySku(String sku, UpdateProductRequest updateRequest) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (updateRequest.getName() != null) {
            product.setName(updateRequest.getName());
        }
        if (updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategory(category);
        }
        if (updateRequest.getDescription() != null) {
            product.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getPrice() != null) {
            product.setPrice(updateRequest.getPrice());
        }
        if (updateRequest.getStock() != null) {
            product.setStock(updateRequest.getStock());
        }

        return productRepository.save(product);
    }

    /**
     * Searches for products by name (case-insensitive partial match).
     *
     * @param name the name or partial name to search for
     * @return a list of products whose names contain the search term
     */
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Add image to product using image URL.
     *
     * @param productId the product ID
     * @param request the image request containing image URL
     * @return the added product image
     * @throws ResourceNotFoundException if product is not found
     */
    public ProductImage addImageToProduct(Integer productId, AddImageRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        ProductImage image = new ProductImage();
        image.setImageUrl(request.getImageUrl());
        image.setProduct(product);
        
        return productImageRepository.save(image);
    }

    /**
     * Delete product image.
     *
     * @param imageId the image ID to delete
     * @throws ResourceNotFoundException if image is not found
     */
    public void deleteImage(Integer imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));
        
        try {
            cloudinaryService.deleteImage(image.getImageUrl());
        } catch (Exception e) {
            System.err.println("Failed to delete image from Cloudinary: " + e.getMessage());
        }
        
        productImageRepository.deleteById(imageId);
    }

    /**
     * Upload image file to product.
     *
     * @param productId the product ID
     * @param file the image file to upload
     * @return the uploaded product image
     * @throws ResourceNotFoundException if product is not found
     * @throws RuntimeException if upload fails
     */
    public ProductImage uploadImageToProduct(Integer productId, org.springframework.web.multipart.MultipartFile file) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        try {
            String imageUrl = cloudinaryService.uploadImage(file);
            
            ProductImage image = new ProductImage();
            image.setImageUrl(imageUrl);
            image.setProduct(product);
            
            return productImageRepository.save(image);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }

    /**
     * Get all images for a product.
     *
     * @param productId the product ID
     * @return list of product images
     * @throws ResourceNotFoundException if product is not found
     */
    public List<ProductImage> getProductImages(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return product.getImages();
    }
}
