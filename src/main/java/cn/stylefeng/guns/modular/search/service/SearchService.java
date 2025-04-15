package cn.stylefeng.guns.modular.search.service;

import java.util.List;
import java.util.Map;

/**
 * 搜索服务接口
 */
public interface SearchService {
    
    /**
     * 网页搜索
     * @param query 搜索查询
     * @param filters 过滤条件
     * @return 搜索结果
     */
    List<Map<String, Object>> webSearch(String query, Map<String, Object> filters);
    
    /**
     * 新闻搜索
     * @param query 搜索查询
     * @param filters 过滤条件
     * @return 搜索结果
     */
    List<Map<String, Object>> newsSearch(String query, Map<String, Object> filters);
    
    /**
     * 学术搜索
     * @param query 搜索查询
     * @param filters 过滤条件
     * @return 搜索结果
     */
    List<Map<String, Object>> academicSearch(String query, Map<String, Object> filters);
    
    /**
     * 获取搜索建议
     * @param query 搜索查询
     * @return 搜索建议列表
     */
    List<String> getSearchSuggestions(String query);
} 