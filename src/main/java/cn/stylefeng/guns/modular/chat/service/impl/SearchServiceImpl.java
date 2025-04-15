package cn.stylefeng.guns.modular.chat.service.impl;

import cn.stylefeng.guns.modular.chat.service.SearchService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    
    @Value("${search.api.url}")
    private String searchApiUrl;
    
    @Value("${search.api.key}")
    private String searchApiKey;
    
    @Value("${search.api.default_page_size:10}")
    private int defaultPageSize;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public List<Map<String, Object>> search(String query) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(searchApiUrl)
                .queryParam("q", query)
                .queryParam("key", searchApiKey)
                .build()
                .toUriString();
                
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return extractSearchResults(response);
        } catch (Exception e) {
            throw new RuntimeException("执行搜索失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, Object> advancedSearch(String query, Map<String, Object> filters, 
                                             String sortBy, String sortOrder, 
                                             int page, int pageSize) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(searchApiUrl)
                .queryParam("q", query)
                .queryParam("key", searchApiKey)
                .queryParam("page", page)
                .queryParam("pageSize", pageSize);
                
            if (sortBy != null && !sortBy.isEmpty()) {
                builder.queryParam("sortBy", sortBy);
            }
            
            if (sortOrder != null && !sortOrder.isEmpty()) {
                builder.queryParam("sortOrder", sortOrder);
            }
            
            if (filters != null && !filters.isEmpty()) {
                builder.queryParam("filters", objectMapper.writeValueAsString(filters));
            }
            
            String url = builder.build().toUriString();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            Map<String, Object> result = new HashMap<>();
            result.put("results", extractSearchResults(response));
            result.put("total", response.getOrDefault("total", 0));
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("totalPages", calculateTotalPages(
                (Integer) response.getOrDefault("total", 0), 
                pageSize
            ));
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("执行高级搜索失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<String> getSuggestions(String prefix, int limit) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(searchApiUrl + "/suggestions")
                .queryParam("prefix", prefix)
                .queryParam("limit", limit)
                .queryParam("key", searchApiKey)
                .build()
                .toUriString();
                
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return extractSuggestions(response);
        } catch (Exception e) {
            throw new RuntimeException("获取搜索建议失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<String> getRelatedSearches(String query, int limit) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(searchApiUrl + "/related")
                .queryParam("q", query)
                .queryParam("limit", limit)
                .queryParam("key", searchApiKey)
                .build()
                .toUriString();
                
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return extractRelatedSearches(response);
        } catch (Exception e) {
            throw new RuntimeException("获取相关搜索失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, Object> getSearchStats(String query) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(searchApiUrl + "/stats")
                .queryParam("q", query)
                .queryParam("key", searchApiKey)
                .build()
                .toUriString();
                
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return extractSearchStats(response);
        } catch (Exception e) {
            throw new RuntimeException("获取搜索统计失败: " + e.getMessage(), e);
        }
    }
    
    private List<Map<String, Object>> extractSearchResults(Map<String, Object> response) {
        if (response == null || !response.containsKey("results")) {
            return new ArrayList<>();
        }
        
        return (List<Map<String, Object>>) response.get("results");
    }
    
    private List<String> extractSuggestions(Map<String, Object> response) {
        if (response == null || !response.containsKey("suggestions")) {
            return new ArrayList<>();
        }
        
        return (List<String>) response.get("suggestions");
    }
    
    private List<String> extractRelatedSearches(Map<String, Object> response) {
        if (response == null || !response.containsKey("related")) {
            return new ArrayList<>();
        }
        
        return (List<String>) response.get("related");
    }
    
    private Map<String, Object> extractSearchStats(Map<String, Object> response) {
        if (response == null) {
            return new HashMap<>();
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_searches", response.getOrDefault("total_searches", 0));
        stats.put("unique_searches", response.getOrDefault("unique_searches", 0));
        stats.put("avg_time", response.getOrDefault("avg_time", 0));
        stats.put("top_queries", response.getOrDefault("top_queries", new ArrayList<>()));
        
        return stats;
    }
    
    private int calculateTotalPages(int total, int pageSize) {
        if (pageSize <= 0) {
            pageSize = defaultPageSize;
        }
        
        return (int) Math.ceil((double) total / pageSize);
    }
} 