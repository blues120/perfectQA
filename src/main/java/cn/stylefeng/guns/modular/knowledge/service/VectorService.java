package cn.stylefeng.guns.modular.knowledge.service;

import cn.stylefeng.guns.modular.knowledge.entity.TextVector;

import java.util.List;

/**
 * 向量服务接口
 */
public interface VectorService {
    
    /**
     * 添加文档向量
     * @param documents 文档向量列表
     */
    void addDocuments(List<TextVector> documents);

    /**
     * 搜索相似文档
     * @param query 查询文本
     * @param topK 返回结果数量
     * @return 相似文档列表
     */
    List<TextVector> searchSimilar(String query, int topK);

    /**
     * 删除文档向量
     * @param documentId 文档ID
     */
    void deleteDocument(String documentId);

    /**
     * 更新文档向量
     * @param documentId 文档ID
     * @param document 文档向量
     */
    void updateDocument(String documentId, TextVector document);
} 