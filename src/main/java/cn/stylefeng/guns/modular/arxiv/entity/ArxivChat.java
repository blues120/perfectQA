package cn.stylefeng.guns.modular.arxiv.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Arxiv论文聊天记录实体
 */
@Data
@TableName("arxiv_chats")
public class ArxivChat {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 论文ID
     */
    @TableField("paper_id")
    private String paperId;

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

    /**
     * 逻辑删除字段
     */
    @TableLogic
    @TableField(value = "deleted")
    private Integer deleted = 0;
}
