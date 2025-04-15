package cn.stylefeng.guns.modular.chat.service.impl;

import cn.stylefeng.guns.modular.chat.service.VectorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class VectorServiceImpl implements VectorService {
    
    @Value("${vector.api.url}")
    private String vectorApiUrl;
    
    @Value("${vector.api.key}")
    private String vectorApiKey;
    
    @Value("${vector.api.timeout:30}")
    private int timeout;
    
    @Value("${vector.api.dimension:1536}")
    private int dimension;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    
    @Override
    public List<Double> createVector(String text) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("text", text);
            request.put("api_key", vectorApiKey);
            
            Map<String, Object> response = restTemplate.postForObject(
                vectorApiUrl + "/embed",
                request,
                Map.class
            );
            
            return extractVector(response);
        } catch (Exception e) {
            throw new RuntimeException("创建向量失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<List<Double>> createVectors(List<String> texts) {
        try {
            List<CompletableFuture<List<Double>>> futures = texts.stream()
                .map(text -> CompletableFuture.supplyAsync(
                    () -> createVector(text), 
                    executorService
                ))
                .collect(Collectors.toList());
                
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(timeout, TimeUnit.SECONDS);
                
            return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        } catch (TimeoutException e) {
            throw new RuntimeException("创建向量超时: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("批量创建向量失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public double calculateSimilarity(List<Double> vector1, List<Double> vector2) {
        if (vector1 == null || vector2 == null || 
            vector1.size() != vector2.size() || 
            vector1.isEmpty() || vector2.isEmpty()) {
            return 0.0;
        }
        
        // 计算余弦相似度
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vector1.size(); i++) {
            dotProduct += vector1.get(i) * vector2.get(i);
            norm1 += vector1.get(i) * vector1.get(i);
            norm2 += vector2.get(i) * vector2.get(i);
        }
        
        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);
        
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (norm1 * norm2);
    }
    
    @Override
    public List<Map<String, Object>> findMostSimilar(List<Double> queryVector, 
                                                    List<List<Double>> vectors, 
                                                    int topK) {
        if (queryVector == null || vectors == null || vectors.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        for (int i = 0; i < vectors.size(); i++) {
            List<Double> vector = vectors.get(i);
            double similarity = calculateSimilarity(queryVector, vector);
            
            Map<String, Object> result = new HashMap<>();
            result.put("index", i);
            result.put("similarity", similarity);
            results.add(result);
        }
        
        return results.stream()
            .sorted((a, b) -> Double.compare(
                (Double) b.get("similarity"), 
                (Double) a.get("similarity")
            ))
            .limit(topK)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Map<String, Object>> findMostSimilarTexts(String queryText, 
                                                         List<String> texts, 
                                                         int topK) {
        if (queryText == null || texts == null || texts.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Double> queryVector = createVector(queryText);
        List<List<Double>> vectors = createVectors(texts);
        
        List<Map<String, Object>> results = findMostSimilar(queryVector, vectors, topK);
        
        // 添加文本内容到结果中
        for (Map<String, Object> result : results) {
            int index = (Integer) result.get("index");
            result.put("text", texts.get(index));
        }
        
        return results;
    }
    
    @Override
    public void storeVector(String id, List<Double> vector, Map<String, Object> metadata) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("id", id);
            request.put("vector", vector);
            request.put("metadata", metadata);
            request.put("api_key", vectorApiKey);
            
            restTemplate.postForObject(
                vectorApiUrl + "/store",
                request,
                Map.class
            );
        } catch (Exception e) {
            throw new RuntimeException("存储向量失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, Object> retrieveVector(String id) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(vectorApiUrl + "/retrieve")
                .queryParam("id", id)
                .queryParam("api_key", vectorApiKey)
                .build()
                .toUriString();
                
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("检索向量失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteVector(String id) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(vectorApiUrl + "/delete")
                .queryParam("id", id)
                .queryParam("api_key", vectorApiKey)
                .build()
                .toUriString();
                
            restTemplate.delete(url);
        } catch (Exception e) {
            throw new RuntimeException("删除向量失败: " + e.getMessage(), e);
        }
    }
    
    private List<Double> extractVector(Map<String, Object> response) {
        if (response == null || !response.containsKey("vector")) {
            return new ArrayList<>();
        }
        
        return (List<Double>) response.get("vector");
    }
} 