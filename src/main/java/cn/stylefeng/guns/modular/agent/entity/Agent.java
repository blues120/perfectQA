package cn.stylefeng.guns.modular.agent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 智能代理实体类
 */
@Data
@TableName("agents")
public class Agent {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 代理名称
     */
    @TableField("name")
    private String name;

    /**
     * 代理描述
     */
    @TableField("description")
    private String description;

    /**
     * 代理配置（JSON格式）
     */
    @TableField("config")
    private String config;

    /**
     * 代理类型
     */
    @TableField("type")
    private String type;

    /**
     * 代理状态
     */
    @TableField("status")
    private String status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建用户
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 是否启用
     */
    @TableField("is_enabled")
    private Boolean isEnabled;

    /**
     * 排序序号
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 逻辑删除字段
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted = 0;
}
