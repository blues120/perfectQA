package cn.stylefeng.guns.modular.chathistory.controller;

import cn.stylefeng.roses.kernel.rule.pojo.response.ErrorResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.guns.modular.chathistory.entity.ChatHistory;
import cn.stylefeng.guns.modular.chathistory.service.ChatHistoryService;
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
     * 分页查询会话历史记录
     */
    @GetMapping("/session/{sessionId}")
    public ResponseData<Page<ChatHistory>> findBySessionId(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {
        return new SuccessResponseData<>(chatHistoryService.findBySessionId(current, size, sessionId));
    }

    /**
     * 分页查询用户历史记录
     */
    @GetMapping("/user/{userId}")
    public ResponseData<Page<ChatHistory>> findByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {
        return new SuccessResponseData<>(chatHistoryService.findByUserId(current, size, userId));
    }

    /**
     * 搜索聊天历史记录
     */
    @GetMapping("/search")
    public ResponseData<Page<ChatHistory>> searchChatHistory(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {
        return new SuccessResponseData<>(chatHistoryService.searchChatHistory(current, size, userId, keyword, startTime, endTime));
    }

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
    @GetMapping("/session/{sessionId}/all")
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
    @GetMapping("/user/{userId}/all")
    public ResponseData<List<ChatHistory>> getChatHistoryByUser(@PathVariable String userId) {
        return new SuccessResponseData<>(chatHistoryService.getChatHistoryByUser(userId));
    }

    /**
     * 获取用户最近的聊天记录
     */
    @GetMapping("/user/{userId}/recent")
    public ResponseData<List<ChatHistory>> getRecentChats(
            @PathVariable String userId,
            @RequestParam(defaultValue = "10") int limit) {
        return new SuccessResponseData<>(chatHistoryService.getRecentChats(userId, limit));
    }

    /**
     * 搜索聊天历史记录（不分页）
     */
    @GetMapping("/search/all")
    public ResponseData<List<ChatHistory>> searchChatHistoryAll(
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
        return new SuccessResponseData<>(chatHistoryService.deleteChatHistory(sessionId));
    }

    /**
     * 删除用户的聊天历史记录
     */
    @DeleteMapping("/user/{userId}")
    public ResponseData<Boolean> deleteChatHistoryByUser(@PathVariable String userId) {
        return new SuccessResponseData<>(chatHistoryService.deleteChatHistoryByUser(userId));
    }

    /**
     * 获取用户频繁提问的问题
     */
    @GetMapping("/user/{userId}/frequent-questions")
    public ResponseData<List<String>> getFrequentQuestions(@PathVariable String userId) {
        return new SuccessResponseData<>(chatHistoryService.getFrequentQuestions(userId));
    }

    /**
     * 获取相关问题
     */
    @GetMapping("/related-questions")
    public ResponseData<List<String>> getRelatedQuestions(@RequestParam String question) {
        return new SuccessResponseData<>(chatHistoryService.getRelatedQuestions(question));
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
            return new ErrorResponseData<>("408","导出失败：" + e.getMessage());
        }
    }

    /**
     * 清理旧聊天记录
     */
    @DeleteMapping("/clear-old")
    public ResponseData<Boolean> clearOldChats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime before) {
        return new SuccessResponseData<>(chatHistoryService.clearOldChats(before));
    }
}
