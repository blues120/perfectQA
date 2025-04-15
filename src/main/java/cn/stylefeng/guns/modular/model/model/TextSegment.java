package cn.stylefeng.guns.modular.model.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class TextSegment {
    private String text;
    private List<Float> embedding;
    private Map<String, Object> metadata;
}
