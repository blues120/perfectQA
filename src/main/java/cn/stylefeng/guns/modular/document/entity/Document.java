package cn.stylefeng.guns.modular.document.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档实体类
 */
@Data
@TableName("documents")
public class Document {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件名
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件ID
     */
    @TableField("file_id")
    private String fileId;

    /**
     * 内容类型
     */
    @TableField("content_type")
    private String contentType;

    /**
     * 文档内容
     */
    @TableField("content")
    private String content;

    /**
     * 区域
     */
    @TableField("district")
    private String district;

    /**
     * 部门
     */
    @TableField("department")
    private String department;

    /**
     * 关键词
     */
    @TableField("keywords")
    private String keywords;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 创建用户
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 分段数量
     */
    @TableField("segment_count")
    private Integer segmentCount;

    /**
     * 文件大小
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 逻辑删除字段
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted = 0;
}
