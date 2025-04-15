package cn.stylefeng.guns.modular.search.service.impl;

import cn.stylefeng.guns.modular.search.service.SearchService;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {

    private final Map<String, List<Map<String, Object>>> searchHistory = new HashMap<>();
    private final Map<String, Integer> searchCounts = new HashMap<>();

    @Override
    public List<Map<String, Object>> webSearch(String query, Map<String, Object> filters) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        // 实现网页搜索逻辑
        // 这里使用示例数据，实际项目中应该调用搜索引擎API
        if (query != null && !query.isEmpty()) {
            // 模拟搜索结果
            for (int i = 1; i <= 10; i++) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", "web_" + i);
                result.put("title", "搜索结果 " + i + ": " + query);
                result.put("url", "https://example.com/result" + i);
                result.put("snippet", "这是关于 " + query + " 的搜索结果描述...");
                result.put("relevance", 1.0 - (i * 0.1));
                results.add(result);
            }
            
            // 应用过滤器
            if (filters != null) {
                applyFilters(results, filters);
            }
            
            // 记录搜索历史
            recordSearchHistory("web", query, results);
        }
        
        return results;
    }

    @Override
    public List<Map<String, Object>> newsSearch(String query, Map<String, Object> filters) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        // 实现新闻搜索逻辑
        if (query != null && !query.isEmpty()) {
            // 模拟新闻搜索结果
            for (int i = 1; i <= 10; i++) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", "news_" + i);
                result.put("title", "新闻标题 " + i + ": " + query);
                result.put("source", "新闻来源 " + i);
                result.put("date", new Date());
                result.put("content", "这是关于 " + query + " 的新闻内容...");
                result.put("relevance", 1.0 - (i * 0.1));
                results.add(result);
            }
            
            // 应用过滤器
            if (filters != null) {
                applyFilters(results, filters);
            }
            
            // 记录搜索历史
            recordSearchHistory("news", query, results);
        }
        
        return results;
    }

    @Override
    public List<Map<String, Object>> academicSearch(String query, Map<String, Object> filters) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        // 实现学术搜索逻辑
        if (query != null && !query.isEmpty()) {
            // 模拟学术搜索结果
            for (int i = 1; i <= 10; i++) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", "academic_" + i);
                result.put("title", "论文标题 " + i + ": " + query);
                result.put("authors", Arrays.asList("作者1", "作者2", "作者3"));
                result.put("journal", "期刊名称 " + i);
                result.put("year", 2023);
                result.put("abstract", "这是关于 " + query + " 的论文摘要...");
                result.put("relevance", 1.0 - (i * 0.1));
                results.add(result);
            }
            
            // 应用过滤器
            if (filters != null) {
                applyFilters(results, filters);
            }
            
            // 记录搜索历史
            recordSearchHistory("academic", query, results);
        }
        
        return results;
    }

    @Override
    public List<String> getSearchSuggestions(String query) {
        List<String> suggestions = new ArrayList<>();
        
        // 实现搜索建议逻辑
        if (query != null && !query.isEmpty()) {
            // 模拟搜索建议
            suggestions.add(query + " 相关");
            suggestions.add(query + " 教程");
            suggestions.add(query + " 最新");
            suggestions.add(query + " 2023");
            suggestions.add(query + " 研究");
        }
        
        return suggestions;
    }
    
    // 私有方法：应用过滤器
    private void applyFilters(List<Map<String, Object>> results, Map<String, Object> filters) {
        Iterator<Map<String, Object>> iterator = results.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> result = iterator.next();
            boolean shouldRemove = false;
            
            for (Map.Entry<String, Object> entry : filters.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                
                if (result.containsKey(key)) {
                    Object resultValue = result.get(key);
                    if (!resultValue.equals(value)) {
                        shouldRemove = true;
                        break;
                    }
                }
            }
            
            if (shouldRemove) {
                iterator.remove();
            }
        }
    }
    
    // 私有方法：记录搜索历史
    private void recordSearchHistory(String type, String query, List<Map<String, Object>> results) {
        // 更新搜索计数
        searchCounts.merge(query, 1, Integer::sum);
        
        // 记录搜索历史
        Map<String, Object> historyEntry = new HashMap<>();
        historyEntry.put("type", type);
        historyEntry.put("query", query);
        historyEntry.put("timestamp", new Date());
        historyEntry.put("resultCount", results.size());
        
        String timeKey = new Date().toString();
        searchHistory.computeIfAbsent(timeKey, k -> new ArrayList<>()).add(historyEntry);
    }
    
    // 新增方法：获取热门搜索
    public List<Map<String, Object>> getPopularSearches(int limit) {
        List<Map<String, Object>> popularSearches = new ArrayList<>();
        
        searchCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limit)
            .forEach(entry -> {
                Map<String, Object> search = new HashMap<>();
                search.put("query", entry.getKey());
                search.put("count", entry.getValue());
                popularSearches.add(search);
            });
        
        return popularSearches;
    }
    
    // 新增方法：获取搜索历史
    public List<Map<String, Object>> getSearchHistory(String timeRange) {
        return searchHistory.getOrDefault(timeRange, new ArrayList<>());
    }
    
    // 新增方法：清除搜索历史
    public void clearSearchHistory() {
        searchHistory.clear();
        searchCounts.clear();
    }
} 