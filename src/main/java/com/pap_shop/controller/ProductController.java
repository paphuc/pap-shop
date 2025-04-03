package com.pap_shop.controller;

import com.pap_shop.entity.Product;
import com.pap_shop.model.ProductDTO;
import com.pap_shop.service.ProductService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody ProductDTO productDTO) {
        Product savedProduct = productService.addProduct(productDTO);
        return ResponseEntity.ok(savedProduct);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductByID(@PathVariable("id") Integer ID){
        Optional<Product> product = productService.getProductsByID(ID);
        return product.map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{categoryID}")
    public ResponseEntity<List<Product>> getProductByCategoryID(@PathVariable Integer categoryID){
        List<Product> products = productService.getProductsByCatogoryID(categoryID);
        return ResponseEntity.ok(products);
    }
//    @DeleteMapping
//    public void delAllProducts(){
//        productService.delALlProducts();
//    }
}
