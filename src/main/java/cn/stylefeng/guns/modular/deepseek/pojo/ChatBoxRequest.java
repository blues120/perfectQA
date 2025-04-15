package cn.stylefeng.guns.modular.deepseek.pojo;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 任务列表封装类
 *
 * @author ideal
 * @date 2023/05/16 17:42
 */
@Data
public class ChatBoxRequest {
    /**
     * token
     */
    @ChineseDescription("token")
    private String token;

    /**
     * 父任务id
     */
    @ChineseDescription("父任务id")
    private String parentId;

    /**
     * uuid
     */
    @ChineseDescription("uuid")
    private String uuid;

    /**
     * 模型名称
     */
    @ChineseDescription("模型名称")
    private String model;

    /**
     * 消息列表
     */
    @ChineseDescription("消息列表")
    @NotEmpty(message = "messages不能为空，且用户有需要咨询的问题")
    private List<ChatBoxMessage> messages;

    /**
     * 严谨与想象程度
     */
    @ChineseDescription("严谨与想象程度")
    private double temperature;

    /**
     * 语言
     */
    @ChineseDescription("语言")
    private String language;

    /**
     * 是否流式传输
     */
    @ChineseDescription("是否流式传输")
    private boolean stream;

}
