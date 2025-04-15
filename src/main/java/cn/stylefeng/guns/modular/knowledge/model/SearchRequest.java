package cn.stylefeng.guns.modular.knowledge.model;

import lombok.Data;

import java.util.List;

/**
 * 搜索请求模型
 */
@Data
public class SearchRequest {
    
    /**
     * 搜索关键词
     */
    private String keyword;
    
    /**
     * 知识库ID
     */
    private Long knowledgeBaseId;
    
    /**
     * 文档类型
     */
    private String documentType;
    
    /**
     * 标签列表
     */
    private List<String> tags;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;
    
    /**
     * 排序字段
     */
    private String sortField;
    
    /**
     * 排序方式
     */
    private String sortOrder;
    
    /**
     * 页码
     */
    private Integer pageNum;
    
    /**
     * 每页大小
     */
    private Integer pageSize;
} 