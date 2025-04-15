package cn.stylefeng.guns.modular.instruction.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 指令实体类
 */
@Data
@TableName("instructions")
public class Instruction {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 指令名称
     */
    @TableField("name")
    private String name;

    /**
     * 指令内容
     */
    @TableField("content")
    private String content;

    /**
     * 指令类型
     */
    @TableField("type")
    private String type;

    /**
     * 指令描述
     */
    @TableField("description")
    private String description;

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
     * 是否系统指令
     */
    @TableField("is_system")
    private Boolean isSystem;

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
