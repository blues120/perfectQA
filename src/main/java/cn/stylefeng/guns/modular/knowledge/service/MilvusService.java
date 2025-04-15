package cn.stylefeng.guns.modular.knowledge.service;

import cn.stylefeng.guns.modular.knowledge.entity.KnowledgeDocument;

import java.util.List;

/**
 * Milvus向量服务接口
 */
public interface MilvusService {
    /**
     * 插入文档向量
     */
    void insertDocument(KnowledgeDocument document);

    /**
     * 搜索相似文档
     */
    List<String> searchSimilar(String query, int topK);

    /**
     * 删除文档向量
     */
    void deleteDocument(String documentId);

    /**
     * 更新文档向量
     */
    void updateDocument(String documentId, KnowledgeDocument document);

    /**
     * 向量化文本
     */
    String vectorize(String text);
} 