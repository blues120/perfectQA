package cn.stylefeng.guns.modular.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 模型增强实体
 */
@Data
@TableName("model_enhancement")
public class ModelEnhancement {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模型ID
     */
    @TableField("model_id")
    private String modelId;

    /**
     * 增强参数
     */
    @TableField("params")
    private String params;

    /**
     * 增强结果
     */
    @TableField("result")
    private String result;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

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
     * 是否删除
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;
}
