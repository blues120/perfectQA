package cn.stylefeng.guns.modular.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("model")
public class Model {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String name;
    private String type;
    private String description;
    private String version;
    private String framework;
    private String parameters;
    private String inputFormat;
    private String outputFormat;
    private String status;
    private String location;
    private String config;
    private Date createTime;
    private Date updateTime;
    private String createUser;
    private String updateUser;
    private String remark;
} 