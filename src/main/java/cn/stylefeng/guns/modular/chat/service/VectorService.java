package cn.stylefeng.guns.modular.chat.service;

import java.util.List;
import java.util.Map;

public interface VectorService {
    
    /**
     * 创建向量
     * @param text 文本内容
     * @return 向量表示
     */
    List<Double> createVector(String text);
    
    /**
     * 批量创建向量
     * @param texts 文本列表
     * @return 向量列表
     */
    List<List<Double>> createVectors(List<String> texts);
    
    /**
     * 计算向量相似度
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 相似度分数
     */
    double calculateSimilarity(List<Double> vector1, List<Double> vector2);
    
    /**
     * 查找最相似的向量
     * @param queryVector 查询向量
     * @param vectors 向量列表
     * @param topK 返回数量
     * @return 最相似的向量索引和分数
     */
    List<Map<String, Object>> findMostSimilar(List<Double> queryVector, 
                                             List<List<Double>> vectors, 
                                             int topK);
    
    /**
     * 查找最相似的文本
     * @param queryText 查询文本
     * @param texts 文本列表
     * @param topK 返回数量
     * @return 最相似的文本和分数
     */
    List<Map<String, Object>> findMostSimilarTexts(String queryText, 
                                                  List<String> texts, 
                                                  int topK);
    
    /**
     * 将向量存储到数据库
     * @param id 向量ID
     * @param vector 向量数据
     * @param metadata 元数据
     */
    void storeVector(String id, List<Double> vector, Map<String, Object> metadata);
    
    /**
     * 从数据库检索向量
     * @param id 向量ID
     * @return 向量数据和元数据
     */
    Map<String, Object> retrieveVector(String id);
    
    /**
     * 从数据库删除向量
     * @param id 向量ID
     */
    void deleteVector(String id);
} 