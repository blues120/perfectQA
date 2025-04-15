package cn.stylefeng.guns.modular.model.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ModelResponse {
    private String content;
    private List<String> choices;
    private Map<String, Object> metadata;
    private Long promptTokens;
    private Long completionTokens;
    private Long totalTokens;
    private Double processingTime;
    private String model;
    private String finishReason;
}
