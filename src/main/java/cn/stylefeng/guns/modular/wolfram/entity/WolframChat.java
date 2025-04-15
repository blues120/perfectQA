package cn.stylefeng.guns.modular.wolfram.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Wolfram Alpha聊天记录实体
 */
@Data
@TableName("wolfram_chats")
public class WolframChat {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户查询
     */
    @TableField("query")
    private String query;

    /**
     * 系统回答
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
}
