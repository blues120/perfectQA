package cn.stylefeng.guns.modular.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Ollama模型生成实体
 */
@Data
@TableName("ollama_generation")
public class OllamaGeneration {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 提示词
     */
    @TableField("prompt")
    private String prompt;

    /**
     * 生成的文本
     */
    @TableField("generated_text")
    private String generatedText;

    /**
     * 模型名称
     */
    @TableField("model_name")
    private String modelName;

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
