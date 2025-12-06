package com.pap_shop.service;

import com.google.genai.Client;
import com.google.genai.types.EmbedContentConfig;
import com.google.genai.types.EmbedContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for generating text embeddings using Google Gemini API.
 * Provides methods to convert text into vector representations for semantic search.
 */
@Service
public class EmbeddingService {

    private final Client client;

    /**
     * Constructs EmbeddingService with Google Gemini API client.
     *
     * @param apiKey the Google Gemini API key from configuration
     */
    public EmbeddingService(@Value("${gemini.api.key}") String apiKey) {
        this.client = new Client.Builder().apiKey(apiKey).build();
    }

    /**
     * Generates embedding vector for document text.
     * Uses RETRIEVAL_DOCUMENT task type optimized for indexing.
     *
     * @param text the text to embed (product name, description, etc.)
     * @return normalized 768-dimensional embedding vector
     * @throws RuntimeException if embedding generation fails
     */
    public float[] generateEmbedding(String text) {
        EmbedContentConfig config = EmbedContentConfig.builder()
                .taskType("RETRIEVAL_DOCUMENT")
                .outputDimensionality(768)
                .build();

        EmbedContentResponse response = client.models.embedContent(
                "gemini-embedding-001",
                text,
                config
        );

        List<Float> values = response.embeddings()
                .orElseThrow(() -> new RuntimeException("No embeddings returned"))
                .get(0)
                .values()
                .orElseThrow(() -> new RuntimeException("Embedding values not found"));

        float[] embedding = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
            embedding[i] = values.get(i);
        }
        return normalize(embedding);
    }

    /**
     * Generates embedding vector for search query.
     * Uses RETRIEVAL_QUERY task type optimized for searching.
     *
     * @param query the search query text
     * @return normalized 768-dimensional embedding vector
     * @throws RuntimeException if embedding generation fails
     */
    public float[] generateQueryEmbedding(String query) {
        EmbedContentConfig config = EmbedContentConfig.builder()
                .taskType("RETRIEVAL_QUERY")
                .outputDimensionality(768)
                .build();

        EmbedContentResponse response = client.models.embedContent(
                "gemini-embedding-001",
                query,
                config
        );

        List<Float> values = response.embeddings()
                .orElseThrow(() -> new RuntimeException("No embeddings returned"))
                .get(0)
                .values()
                .orElseThrow(() -> new RuntimeException("Embedding values not found"));
        float[] embedding = new float[values.size()];
        for (int i = 0; i < values.size(); i++) {
            embedding[i] = values.get(i);
        }
        return normalize(embedding);
    }

    /**
     * Normalizes a vector to unit length.
     * Required for accurate cosine similarity calculation.
     *
     * @param vector the vector to normalize
     * @return normalized vector with magnitude 1
     */
    private float[] normalize(float[] vector) {
        double norm = 0;
        for (float v : vector) {
            norm += v * v;
        }
        norm = Math.sqrt(norm);

        float[] normalized = new float[vector.length];
        for (int i = 0; i < vector.length; i++) {
            normalized[i] = (float) (vector[i] / norm);
        }
        return normalized;
    }

    /**
     * Calculates cosine similarity between two normalized vectors.
     * Returns value between -1 (opposite) and 1 (identical).
     *
     * @param a first normalized vector
     * @param b second normalized vector
     * @return similarity score (higher means more similar)
     */
    public float cosineSimilarity(float[] a, float[] b) {
        float dotProduct = 0;
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
        }
        return dotProduct;
    }
}
