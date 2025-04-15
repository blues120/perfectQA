package cn.stylefeng.guns.modular.deepseek.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author ☪wl
 * @date 2025/3/11 13:51
 */

@TableName("shenpi_order")
@Data
public class ShenPiOrder implements Serializable {

    private static final long serialVersionUID = 5415377793294795146L;

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time")
    private Date createTime;

    @TableField(exist = false)
    private String type = "城市建筑垃圾";

    @TableField(value = "create_user_id")
    private Long createUserId;

    @TableField("create_user_name")
    private String createUserName;

    @TableField("shen_pi_result")
    private String shenPiResult;

    @TableField(exist = false)
    private String createTimeStart;
    @TableField(exist = false)
    private String createTimeEnd;

}
