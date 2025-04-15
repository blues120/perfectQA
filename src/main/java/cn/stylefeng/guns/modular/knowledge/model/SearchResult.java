package cn.stylefeng.guns.modular.knowledge.model;

import cn.stylefeng.guns.modular.knowledge.entity.KnowledgeDocument;
import lombok.Data;

import java.util.List;

/**
 * 搜索结果模型
 */
@Data
public class SearchResult {
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 当前页码
     */
    private Integer pageNum;
    
    /**
     * 每页大小
     */
    private Integer pageSize;
    
    /**
     * 文档列表
     */
    private List<KnowledgeDocument> documents;
    
    /**
     * 相关标签
     */
    private List<String> relatedTags;
    
    /**
     * 搜索耗时（毫秒）
     */
    private Long searchTime;
    
    /**
     * 是否还有更多结果
     */
    private Boolean hasMore;
} 