package cn.stylefeng.guns.modular.database.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据库聊天记录实体
 */
@Data
@TableName("database_chats")
public class DatabaseChat {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 数据库类型
     */
    @TableField("database_type")
    private String databaseType;

    /**
     * 连接字符串
     */
    @TableField("connection_string")
    private String connectionString;

    /**
     * 查询语句
     */
    @TableField("query")
    private String query;

    /**
     * 查询结果
     */
    @TableField("response")
    private String response;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted = 0;
}
