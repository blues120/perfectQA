package cn.stylefeng.guns.modular.chat.controller;


import cn.stylefeng.guns.modular.chat.entity.ChatHistory;
import cn.stylefeng.guns.modular.chat.service.ChatHistoryService;
import cn.stylefeng.roses.kernel.rule.pojo.response.ErrorResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 聊天历史记录控制器
 */
@RestController
@RequestMapping("/api/chat-history")
public class ChatHistoryController {

    @Autowired
    private ChatHistoryService chatHistoryService;

    /**
     * 创建聊天历史记录
     */
    @PostMapping
    public ResponseData<ChatHistory> saveChatHistory(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        String userId = request.get("userId");
        String question = request.get("question");
        String answer = request.get("answer");
        String district = request.get("district");
        String department = request.get("department");
        String keywords = request.get("keywords");

        ChatHistory chatHistory;
        if (district != null && department != null) {
            chatHistory = chatHistoryService.saveChatHistory(sessionId, userId, question, answer, district, department);
        } else if (keywords != null) {
            chatHistory = chatHistoryService.saveChatHistory(sessionId, userId, question, answer, keywords);
        } else {
            chatHistory = chatHistoryService.saveChatHistory(sessionId, userId, question, answer);
        }

        return new SuccessResponseData<>(chatHistory);
    }

    /**
     * 获取会话的聊天历史记录
     */
    @GetMapping("/session/{sessionId}")
    public ResponseData<List<ChatHistory>> getChatHistoryBySession(@PathVariable String sessionId) {
        return new SuccessResponseData<>(chatHistoryService.getChatHistoryBySession(sessionId));
    }

    /**
     * 获取会话的聊天历史记录（按时间升序）
     */
    @GetMapping("/session/{sessionId}/asc")
    public ResponseData<List<ChatHistory>> getChatHistoryBySessionAsc(@PathVariable String sessionId) {
        return new SuccessResponseData<>(chatHistoryService.getChatHistoryBySessionAsc(sessionId));
    }

    /**
     * 获取用户的聊天历史记录
     */
    @GetMapping("/user/{userId}")
    public ResponseData<List<ChatHistory>> getChatHistoryByUser(@PathVariable String userId) {
        return new SuccessResponseData<>(chatHistoryService.getChatHistoryByUser(userId));
    }

    /**
     * 搜索聊天历史记录
     */
    @GetMapping("/search")
    public ResponseData<List<ChatHistory>> searchChatHistory(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return new SuccessResponseData<>(chatHistoryService.searchChatHistory(userId, keyword, startTime, endTime));
    }

    /**
     * 删除会话的聊天历史记录
     */
    @DeleteMapping("/session/{sessionId}")
    public ResponseData<Boolean> deleteChatHistory(@PathVariable String sessionId) {
        boolean result = chatHistoryService.deleteChatHistory(sessionId);
        return new SuccessResponseData<>(result);
    }

    /**
     * 删除用户的聊天历史记录
     */
    @DeleteMapping("/user/{userId}")
    public ResponseData<Boolean> deleteChatHistoryByUser(@PathVariable String userId) {
        boolean result = chatHistoryService.deleteChatHistoryByUser(userId);
        return new SuccessResponseData<>(result);
    }

    /**
     * 导出聊天历史记录
     */
    @GetMapping("/export/{sessionId}")
    public ResponseData<String> exportChatHistory(@PathVariable String sessionId) {
        try {
            String filePath = chatHistoryService.exportChatHistory(sessionId);
            return new SuccessResponseData<>(filePath);
        } catch (IOException e) {
            return new ErrorResponseData<>("408","导出聊天历史记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户最近的聊天记录
     */
    @GetMapping("/recent/{userId}")
    public ResponseData<List<ChatHistory>> getRecentChats(
            @PathVariable String userId,
            @RequestParam(defaultValue = "10") int limit) {
        return new SuccessResponseData<>(chatHistoryService.getRecentChats(userId, limit));
    }

    /**
     * 获取用户常用问题
     */
    @GetMapping("/frequent-questions/{userId}")
    public ResponseData<List<String>> getFrequentQuestions(@PathVariable String userId) {
        return new SuccessResponseData<>(chatHistoryService.getFrequentQuestions(userId));
    }

    /**
     * 清理旧聊天记录
     */
    @DeleteMapping("/cleanup")
    public ResponseData<Boolean> clearOldChats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime before) {
        boolean result = chatHistoryService.clearOldChats(before);
        return new SuccessResponseData<>(result);
    }

    /**
     * 获取相关问题
     */
    @GetMapping("/related-questions")
    public ResponseData<List<String>> getRelatedQuestions(@RequestParam String question) {
        return new SuccessResponseData<>(chatHistoryService.getRelatedQuestions(question));
    }
}
