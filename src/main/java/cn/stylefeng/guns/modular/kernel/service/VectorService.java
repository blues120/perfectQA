package cn.stylefeng.guns.modular.kernel.service;

import java.util.List;

/**
 * 向量服务接口
 */
public interface VectorService {
    
    /**
     * 将文本转换为向量
     * @param text 输入文本
     * @return 向量数组
     */
    float[] vectorize(String text);

    /**
     * 批量将文本转换为向量
     * @param texts 输入文本列表
     * @return 向量数组列表
     */
    List<float[]> vectorizeBatch(List<String> texts);

    /**
     * 计算两个向量的相似度
     * @param vector1 第一个向量
     * @param vector2 第二个向量
     * @return 相似度值
     */
    float calculateSimilarity(float[] vector1, float[] vector2);

    /**
     * 保存向量到向量数据库
     * @param collectionName 集合名称
     * @param vector 向量数据
     * @param id 向量ID
     */
    void saveVector(String collectionName, float[] vector, String id);

    /**
     * 搜索相似向量
     * @param collectionName 集合名称
     * @param vector 查询向量
     * @param topK 返回结果数量
     * @return 相似向量ID列表
     */
    List<String> searchSimilar(String collectionName, float[] vector, int topK);
} 