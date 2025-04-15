package cn.stylefeng.guns.modular.knowledge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("knowledge_extraction")
public class KnowledgeExtraction {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String title;
    private String content;
    private String source;
    private String category;
    private String keywords;
    private String entities;
    private String relations;
    private String status;
    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;
    private String remark;
} 