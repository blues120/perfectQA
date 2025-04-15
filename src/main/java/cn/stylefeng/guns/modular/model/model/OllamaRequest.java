package cn.stylefeng.guns.modular.model.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Data
public class OllamaRequest {
    @NotBlank(message = "模型名称不能为空")
    private String model;

    @NotBlank(message = "提示词不能为空")
    private String prompt;

    private List<String> context;
    private Map<String, Object> options;
    private String system;
    private String template;
    private Boolean stream;
    private Boolean raw;
    private String format;
}
