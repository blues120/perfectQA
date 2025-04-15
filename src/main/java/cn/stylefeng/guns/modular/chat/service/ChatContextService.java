package cn.stylefeng.guns.modular.chat.service;

import java.util.List;
import java.util.Map;

/**
 * 聊天上下文服务接口
 */
public interface ChatContextService {
    
    /**
     * 获取聊天上下文
     * @param sessionId 会话ID
     * @return 聊天上下文
     */
    List<Map<String, Object>> getChatContext(String sessionId);
    
    /**
     * 保存聊天消息
     * @param sessionId 会话ID
     * @param message 消息内容
     * @return 保存结果
     */
    boolean saveChatMessage(String sessionId, Map<String, Object> message);
    
    /**
     * 清除聊天上下文
     * @param sessionId 会话ID
     * @return 清除结果
     */
    boolean clearChatContext(String sessionId);
    
    /**
     * 获取会话历史
     * @param sessionId 会话ID
     * @param limit 限制数量
     * @return 会话历史
     */
    List<Map<String, Object>> getChatHistory(String sessionId, int limit);
    
    /**
     * 更新会话状态
     * @param sessionId 会话ID
     * @param status 状态
     * @return 更新结果
     */
    boolean updateSessionStatus(String sessionId, String status);
    
    /**
     * 获取会话信息
     * @param sessionId 会话ID
     * @return 会话信息
     */
    Map<String, Object> getSessionInfo(String sessionId);

    /**
     * 分析聊天内容
     * @param sessionId 会话ID
     * @return 分析结果
     */
    Map<String, Object> analyzeChat(String sessionId);
} 