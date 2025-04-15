package cn.stylefeng.guns.modular.model.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Data
public class ModelRequest {
    @NotBlank(message = "模型名称不能为空")
    private String modelName;

    @NotBlank(message = "提示词不能为空")
    private String prompt;

    private List<String> context;
    private Map<String, Object> parameters;
    private String systemPrompt;
    private Integer maxTokens;
    private Double temperature;
    private Double topP;
    private Integer n;
    private Boolean stream;
    private String apiKey;
    private List<Map<String, String>> messages;
}
