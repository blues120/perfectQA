package cn.stylefeng.guns.modular.texttoimage.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文字生成图片实体
 */
@Data
@TableName("text_to_images")
public class TextToImage {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 提示词
     */
    @TableField(value = "prompt")
    private String prompt;

    /**
     * 图片URL
     */
    @TableField(value = "image_url")
    private String imageUrl;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 逻辑删除字段
     */
    @TableLogic
    @TableField(value = "deleted")
    private Integer deleted = 0;
}
