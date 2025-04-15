package cn.stylefeng.guns.modular.knowledge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识文档实体类
 */
@Data
@TableName("knowledge_document")
public class KnowledgeDocument {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 知识库ID
     */
    private Long knowledgeBaseId;
    
    /**
     * 文档名称
     */
    private String name;
    
    /**
     * 原始文件名
     */
    private String originalName;
    
    /**
     * 存储文件名
     */
    private String fileName;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * 文件路径
     */
    private String filePath;
    
    /**
     * 文档内容
     */
    private String content;
    
    /**
     * 文档大小（字节）
     */
    private Long size;
    
    /**
     * 状态：0-未处理，1-处理中，2-处理完成，3-处理失败
     */
    private Integer status;
    
    /**
     * 处理进度
     */
    private Integer progress;
    
    /**
     * 处理结果
     */
    private String result;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 创建人
     */
    private Long createUser;
    
    /**
     * 更新人
     */
    private Long updateUser;
} 