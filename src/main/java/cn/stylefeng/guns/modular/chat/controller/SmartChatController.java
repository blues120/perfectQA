package cn.stylefeng.guns.modular.chat.controller;

import cn.stylefeng.guns.modular.model.model.ApiResponse;
import cn.stylefeng.guns.modular.common.service.SmartChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 智能聊天控制器
 */
@RestController
@RequestMapping("/api/smart-chat")
public class SmartChatController {

    @Autowired
    private SmartChatService smartChatService;

    @PostMapping("/chat")
    public ApiResponse<String> chat(
            @RequestParam String userId,
            @RequestParam String sessionId,
            @RequestParam String question,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String modelName) {
        try {
            String response = smartChatService.chat(userId, sessionId, question, district, department, modelName);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("聊天处理失败：" + e.getMessage());
        }
    }

    @GetMapping("/context")
    public ApiResponse<Map<String, Object>> getChatContext(
            @RequestParam String userId) {
        try {
            Map<String, Object> context = smartChatService.getChatContext(userId);
            return ApiResponse.success(context);
        } catch (Exception e) {
            return ApiResponse.error("获取对话上下文失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/context")
    public ApiResponse<Boolean> clearChatContext(
            @RequestParam String userId) {
        try {
            Boolean result = smartChatService.clearChatContext(userId);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("清空对话上下文失败：" + e.getMessage());
        }
    }

    @PostMapping("/analyze")
    public ApiResponse<Map<String, Object>> analyzeChat(
            @RequestParam String message) {
        try {
            Map<String, Object> analysis = smartChatService.analyzeChat(message);
            return ApiResponse.success(analysis);
        } catch (Exception e) {
            return ApiResponse.error("分析对话内容失败：" + e.getMessage());
        }
    }

    @GetMapping("/suggestions")
    public ApiResponse<List<String>> getSuggestedQuestions(
            @RequestParam String userId,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String department) {
        try {
            List<String> suggestions = smartChatService.getSuggestedQuestions(userId, district, department);
            return ApiResponse.success(suggestions);
        } catch (Exception e) {
            return ApiResponse.error("获取建议问题失败：" + e.getMessage());
        }
    }

    @GetMapping("/models")
    public ApiResponse<Map<String, String>> getAvailableModels() {
        try {
            Map<String, String> models = smartChatService.getAvailableModels();
            return ApiResponse.success(models);
        } catch (Exception e) {
            return ApiResponse.error("获取可用模型失败：" + e.getMessage());
        }
    }
}
