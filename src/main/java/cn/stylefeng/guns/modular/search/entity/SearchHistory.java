package cn.stylefeng.guns.modular.search.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("search_history")
public class SearchHistory {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String query;
    private String userId;
    private String searchType;
    private String filters;
    private String results;
    private Integer resultCount;
    private String status;
    private Date searchTime;
    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;
    private String remark;
} 