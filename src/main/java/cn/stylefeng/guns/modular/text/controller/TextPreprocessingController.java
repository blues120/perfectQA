package cn.stylefeng.guns.modular.text.controller;

import cn.stylefeng.guns.modular.model.model.ApiResponse;
import cn.stylefeng.guns.modular.text.model.TextPreprocessingRequest;
import cn.stylefeng.guns.modular.text.service.TextPreprocessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/text")
public class TextPreprocessingController {

    @Autowired
    private TextPreprocessingService textPreprocessingService;

    @PostMapping("/preprocess")
    public ApiResponse<String> preprocessText(@Validated @RequestBody TextPreprocessingRequest request) {
        try {
            String processedText = textPreprocessingService.preprocessText(request.getText());
            return ApiResponse.success(processedText);
        } catch (Exception e) {
            return ApiResponse.error("文本预处理失败：" + e.getMessage());
        }
    }

    @PostMapping("/split/sentences")
    public ApiResponse<List<String>> splitIntoSentences(@Validated @RequestBody TextPreprocessingRequest request) {
        try {
            List<String> sentences = textPreprocessingService.splitIntoSentences(request.getText());
            return ApiResponse.success(sentences);
        } catch (Exception e) {
            return ApiResponse.error("句子分割失败：" + e.getMessage());
        }
    }

    @PostMapping("/split/paragraphs")
    public ApiResponse<List<String>> splitIntoParagraphs(@Validated @RequestBody TextPreprocessingRequest request) {
        try {
            List<String> paragraphs = textPreprocessingService.splitIntoParagraphs(request.getText());
            return ApiResponse.success(paragraphs);
        } catch (Exception e) {
            return ApiResponse.error("段落分割失败：" + e.getMessage());
        }
    }

    @PostMapping("/chunks")
    public ApiResponse<List<String>> createTextChunks(@Validated @RequestBody TextPreprocessingRequest request) {
        try {
            List<String> chunks = textPreprocessingService.createTextChunks(request.getText(), request.getChunkSize());
            return ApiResponse.success(chunks);
        } catch (Exception e) {
            return ApiResponse.error("文本分块失败：" + e.getMessage());
        }
    }

    @PostMapping("/metadata")
    public ApiResponse<Map<String, Object>> extractMetadata(@Validated @RequestBody TextPreprocessingRequest request) {
        try {
            Map<String, Object> metadata = textPreprocessingService.extractMetadata(request.getText());
            return ApiResponse.success(metadata);
        } catch (Exception e) {
            return ApiResponse.error("元数据提取失败：" + e.getMessage());
        }
    }
}
