package cn.stylefeng.guns.modular.chat.service.impl;

import cn.stylefeng.guns.modular.chat.service.ChatContextService;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ChatContextServiceImpl implements ChatContextService {

    private final Map<String, List<Map<String, Object>>> chatContexts = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> sessionInfo = new ConcurrentHashMap<>();
    private final Map<String, List<Map<String, Object>>> chatHistory = new ConcurrentHashMap<>();
    private final AtomicInteger totalSessions = new AtomicInteger(0);
    private final AtomicInteger activeSessions = new AtomicInteger(0);

    @Override
    public List<Map<String, Object>> getChatContext(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        return chatContexts.getOrDefault(sessionId, Collections.emptyList());
    }

    @Override
    public boolean saveChatMessage(String sessionId, Map<String, Object> message) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }

        // 添加消息元数据
        message.put("timestamp", new Date());
        message.put("messageId", UUID.randomUUID().toString());
        
        // 使用computeIfAbsent确保线程安全
        chatContexts.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(message);
        
        // 更新会话信息
        updateSessionInfo(sessionId, message);
        
        // 保存到历史记录
        saveToHistory(sessionId, message);
        
        return true;
    }

    @Override
    public boolean clearChatContext(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        
        boolean removed = false;
        if (chatContexts.containsKey(sessionId)) {
            chatContexts.remove(sessionId);
            sessionInfo.remove(sessionId);
            activeSessions.decrementAndGet();
            removed = true;
        }
        return removed;
    }

    @Override
    public List<Map<String, Object>> getChatHistory(String sessionId, int limit) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        if (limit < 0) {
            throw new IllegalArgumentException("Limit cannot be negative");
        }

        List<Map<String, Object>> history = chatHistory.getOrDefault(sessionId, Collections.emptyList());
        if (limit > 0 && history.size() > limit) {
            return history.subList(history.size() - limit, history.size());
        }
        return history;
    }

    @Override
    public boolean updateSessionStatus(String sessionId, String status) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }

        Map<String, Object> info = sessionInfo.get(sessionId);
        if (info != null) {
            String oldStatus = (String) info.get("status");
            info.put("status", status);
            info.put("lastUpdate", new Date());
            
            // 更新活跃会话计数
            if ("active".equals(status) && !"active".equals(oldStatus)) {
                activeSessions.incrementAndGet();
            } else if (!"active".equals(status) && "active".equals(oldStatus)) {
                activeSessions.decrementAndGet();
            }
            
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Object> getSessionInfo(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        return sessionInfo.getOrDefault(sessionId, Collections.emptyMap());
    }

    @Override
    public Map<String, Object> analyzeChat(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }

        Map<String, Object> analysis = new HashMap<>();
        List<Map<String, Object>> context = getChatContext(sessionId);
        
        // 分析对话内容
        int totalMessages = context.size();
        int userMessages = 0;
        int assistantMessages = 0;
        Set<String> topics = new HashSet<>();
        Map<String, Integer> keywordCount = new HashMap<>();
        long totalResponseTime = 0;
        int responseCount = 0;
        
        Date lastUserMessageTime = null;
        
        for (Map<String, Object> message : context) {
            String role = (String) message.get("role");
            String content = (String) message.get("content");
            Date timestamp = (Date) message.get("timestamp");
            
            if ("user".equals(role)) {
                userMessages++;
                lastUserMessageTime = timestamp;
                
                // 提取用户消息中的关键词
                extractKeywords(content, keywordCount);
            } else if ("assistant".equals(role)) {
                assistantMessages++;
                
                // 计算响应时间
                if (lastUserMessageTime != null) {
                    totalResponseTime += timestamp.getTime() - lastUserMessageTime.getTime();
                    responseCount++;
                }
                
                // 提取助手消息中的关键词
                extractKeywords(content, keywordCount);
            }
        }
        
        // 计算分析结果
        analysis.put("totalMessages", totalMessages);
        analysis.put("userMessages", userMessages);
        analysis.put("assistantMessages", assistantMessages);
        analysis.put("messageRatio", totalMessages > 0 ? (double) userMessages / totalMessages : 0);
        analysis.put("topKeywords", getTopKeywords(keywordCount, 5));
        analysis.put("averageResponseTime", responseCount > 0 ? totalResponseTime / responseCount : 0);
        analysis.put("sessionDuration", calculateSessionDuration(sessionId));
        analysis.put("messageFrequency", calculateMessageFrequency(context));
        
        return analysis;
    }
    
    // 私有方法：更新会话信息
    private void updateSessionInfo(String sessionId, Map<String, Object> message) {
        Map<String, Object> info = sessionInfo.computeIfAbsent(sessionId, k -> {
            totalSessions.incrementAndGet();
            activeSessions.incrementAndGet();
            return new HashMap<>();
        });
        
        if (info.isEmpty()) {
            info.put("createTime", new Date());
            info.put("status", "active");
            info.put("messageCount", 0);
            info.put("lastActivity", new Date());
        }
        
        info.put("lastMessageTime", new Date());
        info.put("messageCount", (Integer) info.getOrDefault("messageCount", 0) + 1);
        info.put("lastMessageRole", message.get("role"));
        info.put("lastActivity", new Date());
    }
    
    // 私有方法：保存到历史记录
    private void saveToHistory(String sessionId, Map<String, Object> message) {
        chatHistory.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(message);
    }
    
    // 私有方法：获取关键词
    private List<Map<String, Object>> getTopKeywords(Map<String, Integer> keywordCount, int limit) {
        return keywordCount.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> {
                Map<String, Object> keyword = new HashMap<>();
                keyword.put("word", entry.getKey());
                keyword.put("count", entry.getValue());
                return keyword;
            })
            .collect(Collectors.toList());
    }
    
    // 私有方法：计算会话时长
    private long calculateSessionDuration(String sessionId) {
        Map<String, Object> info = sessionInfo.get(sessionId);
        if (info != null) {
            Date createTime = (Date) info.get("createTime");
            Date lastActivity = (Date) info.get("lastActivity");
            return lastActivity.getTime() - createTime.getTime();
        }
        return 0;
    }
    
    // 私有方法：计算消息频率
    private double calculateMessageFrequency(List<Map<String, Object>> context) {
        if (context.size() < 2) {
            return 0;
        }
        
        Date firstMessageTime = (Date) context.get(0).get("timestamp");
        Date lastMessageTime = (Date) context.get(context.size() - 1).get("timestamp");
        long duration = lastMessageTime.getTime() - firstMessageTime.getTime();
        
        return duration > 0 ? (double) context.size() / (duration / 1000.0) : 0;
    }
    
    // 私有方法：提取关键词
    private void extractKeywords(String text, Map<String, Integer> keywordCount) {
        if (text == null || text.isEmpty()) {
            return;
        }
        
        String[] words = text.toLowerCase().split("\\s+");
        for (String word : words) {
            if (word.length() > 2) { // 忽略短词
                keywordCount.merge(word, 1, Integer::sum);
            }
        }
    }
    
    // 新增方法：获取活跃会话
    public List<Map<String, Object>> getActiveSessions() {
        return sessionInfo.entrySet().stream()
            .filter(entry -> "active".equals(entry.getValue().get("status")))
            .map(entry -> {
                Map<String, Object> session = new HashMap<>(entry.getValue());
                session.put("sessionId", entry.getKey());
                return session;
            })
            .collect(Collectors.toList());
    }
    
    // 新增方法：获取会话统计
    public Map<String, Object> getSessionStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalSessions", totalSessions.get());
        stats.put("activeSessions", activeSessions.get());
        stats.put("totalMessages", chatContexts.values().stream()
            .mapToInt(List::size)
            .sum());
        stats.put("averageMessagesPerSession", totalSessions.get() > 0 ? 
            (double) chatContexts.values().stream().mapToInt(List::size).sum() / totalSessions.get() : 0);
        
        // 计算平均会话时长
        long totalDuration = sessionInfo.values().stream()
            .mapToLong(info -> {
                Date createTime = (Date) info.get("createTime");
                Date lastActivity = (Date) info.get("lastActivity");
                return lastActivity.getTime() - createTime.getTime();
            })
            .sum();
        
        stats.put("averageSessionDuration", totalSessions.get() > 0 ? totalDuration / totalSessions.get() : 0);
        
        return stats;
    }
} 