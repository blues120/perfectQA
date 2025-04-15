package cn.stylefeng.guns.modular.knowledge.service;

import cn.stylefeng.guns.modular.knowledge.entity.KnowledgeDocument;
import cn.stylefeng.guns.modular.knowledge.model.SearchRequest;
import cn.stylefeng.guns.modular.knowledge.model.SearchResult;

import java.util.List;

/**
 * 搜索服务接口
 */
public interface SearchService {

    /**
     * 高级搜索
     */
    SearchResult search(SearchRequest request);

    /**
     * 向量相似度搜索
     */
    List<KnowledgeDocument> vectorSearch(String query, int topK);

    /**
     * 获取搜索历史
     */
    List<String> getSearchHistory(Long userId);

    /**
     * 清除搜索历史
     */
    void clearSearchHistory(Long userId);

    /**
     * 保存搜索结果
     */
    void saveSearchResult(Long userId, String query, List<KnowledgeDocument> results);

    /**
     * 获取相关文档
     */
    List<KnowledgeDocument> getRelatedDocuments(Long documentId, int topK);

    /**
     * 更新搜索权重
     */
    void updateSearchWeight(Long documentId, String field, double weight);
} 