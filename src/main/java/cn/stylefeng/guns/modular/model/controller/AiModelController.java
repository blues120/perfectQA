package cn.stylefeng.guns.modular.model.controller;

import cn.stylefeng.guns.modular.model.model.ApiResponse;
import cn.stylefeng.guns.modular.model.model.ModelRequest;
import cn.stylefeng.guns.modular.model.model.ModelResponse;
import cn.stylefeng.guns.modular.model.model.OllamaRequest;
import cn.stylefeng.guns.modular.model.model.OllamaResponse;
import cn.stylefeng.guns.modular.model.service.AiModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * AI模型控制器
 * 提供模型管理、调用和状态查询等功能
 */
@RestController
@RequestMapping("/model")
@Validated
public class AiModelController {

    @Autowired
    private AiModelService aiModelService;

    /**
     * 生成文本
     */
    @PostMapping("/generate")
    public ApiResponse<ModelResponse> generate(@RequestBody @Validated ModelRequest request) {
        try {
            ModelResponse response = aiModelService.generate(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("生成失败：" + e.getMessage());
        }
    }

    /**
     * 对话
     */
    @PostMapping("/chat")
    public ApiResponse<ModelResponse> chat(@RequestBody @Validated ModelRequest request) {
        try {
            ModelResponse response = aiModelService.chat(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("对话失败：" + e.getMessage());
        }
    }

    /**
     * 流式对话
     */
    @PostMapping(value = "/chat/stream", produces = "text/event-stream")
    public SseEmitter streamChat(@RequestBody @Validated ModelRequest request) {
        return aiModelService.streamChat(request);
    }

    /**
     * 向量化
     */
    @PostMapping("/embedding")
    public ApiResponse<ModelResponse> embedding(@RequestBody @Validated ModelRequest request) {
        try {
            ModelResponse response = aiModelService.embedding(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("向量化失败：" + e.getMessage());
        }
    }

    /**
     * 获取可用模型列表
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getAvailableModels() {
        try {
            Map<String, Object> models = aiModelService.getAvailableModels();
            return ApiResponse.success(models);
        } catch (Exception e) {
            return ApiResponse.error("获取模型列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取服务状态
     */
    @GetMapping("/status")
    public ApiResponse<Map<String, Object>> getServiceStatus() {
        try {
            Map<String, Object> status = aiModelService.getServiceStatus();
            return ApiResponse.success(status);
        } catch (Exception e) {
            return ApiResponse.error("获取服务状态失败：" + e.getMessage());
        }
    }

    /**
     * Ollama模型生成
     */
    @PostMapping("/ollama/generate")
    public ApiResponse<OllamaResponse> ollamaGenerate(@RequestBody @Validated OllamaRequest request) {
        try {
            OllamaResponse response = aiModelService.ollamaGenerate(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("Ollama生成失败：" + e.getMessage());
        }
    }

    /**
     * Ollama模型对话
     */
    @PostMapping("/ollama/chat")
    public ApiResponse<OllamaResponse> ollamaChat(@RequestBody @Validated OllamaRequest request) {
        try {
            OllamaResponse response = aiModelService.ollamaChat(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("Ollama对话失败：" + e.getMessage());
        }
    }

    /**
     * Ollama模型流式对话
     */
    @PostMapping(value = "/ollama/chat/stream", produces = "text/event-stream")
    public SseEmitter ollamaStreamChat(@RequestBody @Validated OllamaRequest request) {
        return aiModelService.ollamaStreamChat(request);
    }

    /**
     * Ollama模型向量化
     */
    @PostMapping("/ollama/embedding")
    public ApiResponse<OllamaResponse> ollamaEmbedding(@RequestBody @Validated OllamaRequest request) {
        try {
            OllamaResponse response = aiModelService.ollamaEmbedding(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("Ollama向量化失败：" + e.getMessage());
        }
    }
} 