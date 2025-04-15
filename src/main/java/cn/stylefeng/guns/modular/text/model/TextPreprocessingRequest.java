package cn.stylefeng.guns.modular.text.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class TextPreprocessingRequest {
    @NotBlank(message = "文本不能为空")
    private String text;
    
    private Integer chunkSize;
} 