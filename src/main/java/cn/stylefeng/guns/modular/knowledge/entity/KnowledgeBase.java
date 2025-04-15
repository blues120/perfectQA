package cn.stylefeng.guns.modular.knowledge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识库实体类
 */
@Data
@TableName("knowledge_base")
public class KnowledgeBase {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 知识库名称
     */
    private String name;
    
    /**
     * 知识库描述
     */
    private String description;
    
    /**
     * 知识库类型
     */
    private String type;
    
    /**
     * 向量维度
     */
    private Integer dimension;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 标签列表，以逗号分隔
     */
    private String tags;
    
    /**
     * 权限设置
     */
    private String permissions;
    
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