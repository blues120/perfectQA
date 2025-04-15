package cn.stylefeng.guns.modular.kernel.service;

import cn.stylefeng.guns.modular.knowledge.entity.KnowledgeDocument;
import cn.stylefeng.guns.modular.knowledge.entity.TextVector;

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
    List<TextVector> search(String query, int topK);

    /**
     * 删除文档向量
     */
    void delete(String documentId);

    /**
     * 更新文档向量
     */
    void update(String documentId, KnowledgeDocument document);

    /**
     * 向量化文本
     */
    String vectorize(String text);
} 