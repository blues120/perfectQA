package cn.stylefeng.guns.modular.knowledge.entity;

import lombok.Data;

import java.util.List;

/**
 * 文本向量实体类
 */
@Data
public class TextVector {
    /**
     * 向量ID
     */
    private String id;
    
    /**
     * 文本内容
     */
    private String text;
    
    /**
     * 向量数据
     */
    private List<Float> vector;
    
    /**
     * 相似度分数
     */
    private Float score;
} 