package cn.stylefeng.guns.modular.model.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class OllamaResponse {
    private String model;
    private String response;
    private List<TextSegment> embedding;
    private Boolean done;
    private Long totalDuration;
    private Long loadDuration;
    private Long promptEvalDuration;
    private Long evalDuration;
    private Map<String, Object> context;
    private Map<String, Object> metrics;
}
