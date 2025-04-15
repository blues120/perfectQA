package cn.stylefeng.guns.modular.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("model_configs")
public class ModelConfig {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String baseUrl;

    private String modelName;

    private String description;

    private Boolean isDefault;

    private Boolean isActive;
} 