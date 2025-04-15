package cn.stylefeng.guns.modular.qa.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 问答实体类
 */
@Data
@TableName("qas")
public class QA {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "问题不能为空")
    @Size(max = 2000, message = "问题长度不能超过2000字符")
    @TableField(value = "question", typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String question;

    @NotBlank(message = "答案不能为空")
    @Size(max = 5000, message = "答案长度不能超过5000字符")
    @TableField(value = "answer", typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String answer;

    @TableField("district")
    private String district;

    @TableField("department")
    private String department;

    @TableField("keywords")
    private String keywords;

    @TableField(exist = false)
    private List<String> similarQuestions;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("create_user")
    private String createUser;

    @TableField("is_active")
    private Boolean isActive = true;

    @TableLogic
    @TableField("deleted")
    private Boolean deleted;
}
