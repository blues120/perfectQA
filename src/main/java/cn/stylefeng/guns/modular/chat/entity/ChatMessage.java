package cn.stylefeng.guns.modular.chat.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("chat_messages")
public class ChatMessage {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("session_id")
    private Long sessionId;
    
    @TableField("role")
    private String role; // user/assistant
    
    @TableField("content")
    private String content;
    
    @TableField("type")
    private String type; // text/image/file
    
    @TableField("file_path")
    private String filePath;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableLogic
    @TableField("deleted")
    private Integer deleted = 0;
} 