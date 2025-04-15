package cn.stylefeng.guns.modular.chat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天历史记录实体
 */
@Data
@TableName("chat_history")
public class ChatHistory {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    @TableField(value = "session_id")
    private String sessionId;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 问题内容
     */
    @TableField(value = "question")
    private String question;

    /**
     * 回答内容
     */
    @TableField(value = "answer")
    private String answer;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 区县信息
     */
    @TableField(value = "district")
    private String district;

    /**
     * 部门/科室信息
     */
    @TableField(value = "department")
    private String department;

    /**
     * 关键词
     */
    @TableField(value = "keywords")
    private String keywords;

    /**
     * 逻辑删除字段
     */
    @TableLogic
    @TableField(value = "deleted")
    private Integer deleted = 0;
}
