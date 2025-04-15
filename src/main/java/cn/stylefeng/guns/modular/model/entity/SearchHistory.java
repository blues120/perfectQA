package cn.stylefeng.guns.modular.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 搜索历史实体
 */
@Data
@TableName("search_history")
public class SearchHistory {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 搜索关键词
     */
    @TableField("query")
    private String query;

    /**
     * 搜索引擎
     */
    @TableField("engine")
    private String engine;

    /**
     * 搜索结果
     */
    @TableField("result")
    private String result;

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
