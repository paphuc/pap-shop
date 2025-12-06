package com.pap_shop.service;

import com.pap_shop.entity.Product;
import com.pap_shop.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for semantic product search using embeddings.
 * Provides functionality to search products based on semantic similarity
 * rather than exact keyword matching.
 */
@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepository;
    private final EmbeddingService embeddingService;
    
    /**
     * In-memory cache storing product embeddings for fast retrieval.
     * Key: Product ID, Value: Embedding vector
     */
    private final Map<Integer, float[]> embeddingCache = new HashMap<>();

    /**
     * Initializes embeddings for all products in the database.
     * This method is automatically called after bean construction.
     * Generates and caches embeddings for product name, description, and category.
     */
    @PostConstruct
    public void initEmbeddings() {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            String text = product.getName() + " " + 
                         (product.getDescription() != null ? product.getDescription() : "") + " " +
                         (product.getCategory() != null ? product.getCategory().getName() : "");
            
            float[] embedding = embeddingService.generateEmbedding(text);
            embeddingCache.put(product.getId(), embedding);
            product.setEmbedding(embedding);
        }
    }

    /**
     * Updates the embedding for a specific product.
     * Should be called when product information changes.
     *
     * @param product the product to update embedding for
     */
    public void updateProductEmbedding(Product product) {
        String text = product.getName() + " " + 
                     (product.getDescription() != null ? product.getDescription() : "") + " " +
                     (product.getCategory() != null ? product.getCategory().getName() : "");
        
        float[] embedding = embeddingService.generateEmbedding(text);
        embeddingCache.put(product.getId(), embedding);
        product.setEmbedding(embedding);
    }

    /**
     * Performs semantic search to find products similar to the query.
     * Uses cosine similarity between query embedding and product embeddings.
     *
     * @param query the search query text
     * @param topK the maximum number of results to return
     * @return list of products sorted by similarity score (highest first)
     */
    public List<Product> semanticSearch(String query, int topK) {
        float[] queryEmbedding = embeddingService.generateQueryEmbedding(query);
        
        List<Product> allProducts = productRepository.findAll();
        
        return allProducts.stream()
                .map(product -> {
                    float[] productEmbedding = embeddingCache.get(product.getId());
                    if (productEmbedding == null) {
                        updateProductEmbedding(product);
                        productEmbedding = product.getEmbedding();
                    } else {
                        product.setEmbedding(productEmbedding);
                    }
                    
                    float similarity = embeddingService.cosineSimilarity(queryEmbedding, productEmbedding);
                    return new ProductWithScore(product, similarity);
                })
                .sorted(Comparator.comparingDouble(ProductWithScore::getScore).reversed())
                .limit(topK)
                .map(ProductWithScore::getProduct)
                .collect(Collectors.toList());
    }

    /**
     * Internal class to hold a product with its similarity score.
     */
    private static class ProductWithScore {
        private final Product product;
        private final float score;

        /**
         * Creates a new ProductWithScore.
         *
         * @param product the product
         * @param score the similarity score
         */
        public ProductWithScore(Product product, float score) {
            this.product = product;
            this.score = score;
        }

        public Product getProduct() {
            return product;
        }

        public float getScore() {
            return score;
        }
    }
}
