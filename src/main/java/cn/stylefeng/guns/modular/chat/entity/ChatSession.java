package cn.stylefeng.guns.modular.chat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("chat_sessions")
public class ChatSession {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("session_id")
    private String sessionId;
    
    @TableField("user_id")
    private String userId;
    
    @TableField("type")
    private String type; // LLM/知识库/搜索引擎/文件/数据库/多模态/ARXIV/Wolfram
    
    @TableField("status")
    private String status; // active/closed
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField("last_activity_time")
    private LocalDateTime lastActivityTime;
    
    @TableLogic
    @TableField("deleted")
    private Integer deleted = 0;
} 