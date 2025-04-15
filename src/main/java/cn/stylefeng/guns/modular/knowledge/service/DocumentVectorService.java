package cn.stylefeng.guns.modular.knowledge.service;

import cn.stylefeng.guns.modular.kernel.service.VectorService;
import cn.stylefeng.guns.modular.knowledge.entity.TextVector;

import java.util.List;

/**
 * 文档向量服务接口
 */
public interface DocumentVectorService extends VectorService {
    
    /**
     * 添加文档
     * @param documents 文档列表
     */
    void addDocuments(List<TextVector> documents);
    
    /**
     * 搜索相似文档
     * @param query 查询文本
     * @param topK 返回结果数量
     * @return 相似文档列表
     */
    List<TextVector> searchSimilarDocuments(String query, int topK);
    
    /**
     * 删除文档
     * @param documentId 文档ID
     */
    void deleteDocument(String documentId);
    
    /**
     * 更新文档
     * @param documentId 文档ID
     * @param document 文档内容
     */
    void updateDocument(String documentId, TextVector document);

    /**
     * 向量化文本
     * @param text 输入文本
     * @return 向量ID
     */
    String vectorizeText(String text);
    
    /**
     * 搜索相似文本
     * @param query 查询文本
     * @param limit 返回结果数量限制
     * @return 相似文本列表
     */
    List<String> searchSimilarTexts(String query, int limit);
    
    /**
     * 批量向量化文本
     * @param texts 文本列表
     * @return 向量ID列表
     */
    List<String> batchVectorizeTexts(List<String> texts);
    
    /**
     * 文本向量化
     */
    float[] vectorize(String text);
    
    /**
     * 批量文本向量化
     */
    List<float[]> vectorizeBatch(List<String> texts);
    
    /**
     * 计算向量相似度
     */
    float calculateSimilarity(float[] vector1, float[] vector2);
    
    /**
     * 保存向量到Milvus
     */
    void saveVector(String collectionName, float[] vector, String id);
    
    /**
     * 从Milvus搜索相似向量
     */
    List<String> searchSimilar(String collectionName, float[] vector, int topK);
} 