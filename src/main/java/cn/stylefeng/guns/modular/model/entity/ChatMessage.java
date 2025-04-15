package cn.stylefeng.guns.modular.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天消息实体类
 */
@Data
@TableName("chat_messages")
public class ChatMessage {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 消息类型（USER-用户消息，ASSISTANT-助手消息）
     */
    @TableField("type")
    private String type;

    /**
     * 会话ID
     */
    @TableField("session_id")
    private String sessionId;

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
     * 逻辑删除字段
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted = 0;
}
