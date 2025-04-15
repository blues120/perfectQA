package cn.stylefeng.guns.modular.chat.service;

import java.util.List;
import java.util.Map;

/**
 * 搜索服务接口
 * 提供搜索相关功能
 */
public interface SearchService {
    
    /**
     * 执行搜索
     * @param query 搜索查询
     * @return 搜索结果列表
     */
    List<Map<String, Object>> search(String query);
    
    /**
     * 执行高级搜索
     * @param query 搜索查询
     * @param filters 过滤条件
     * @param sortBy 排序字段
     * @param sortOrder 排序方向
     * @param page 页码
     * @param pageSize 每页大小
     * @return 搜索结果和分页信息
     */
    Map<String, Object> advancedSearch(String query, 
                                     Map<String, Object> filters,
                                     String sortBy, 
                                     String sortOrder,
                                     int page, 
                                     int pageSize);
    
    /**
     * 获取搜索建议
     * @param prefix 前缀
     * @param limit 返回数量
     * @return 建议列表
     */
    List<String> getSuggestions(String prefix, int limit);
    
    /**
     * 获取相关搜索
     * @param query 搜索查询
     * @param limit 返回数量
     * @return 相关搜索列表
     */
    List<String> getRelatedSearches(String query, int limit);
    
    /**
     * 获取搜索统计
     * @param query 搜索查询
     * @return 统计信息
     */
    Map<String, Object> getSearchStats(String query);
} 