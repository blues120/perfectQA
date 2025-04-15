package cn.stylefeng.guns.modular.common.service;

import java.util.List;
import java.util.Map;

/**
 * 智能聊天服务接口
 */
public interface SmartChatService {
    
    /**
     * 聊天对话
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param question 用户问题
     * @param district 地区
     * @param department 部门
     * @param modelName 模型名称
     * @return 回复内容
     */
    String chat(String userId, String sessionId, String question, String district, String department, String modelName);
    
    /**
     * 获取对话上下文
     * @param userId 用户ID
     * @return 对话上下文
     */
    Map<String, Object> getChatContext(String userId);
    
    /**
     * 清空对话上下文
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean clearChatContext(String userId);
    
    /**
     * 分析对话内容
     * @param message 对话内容
     * @return 分析结果
     */
    Map<String, Object> analyzeChat(String message);
    
    /**
     * 获取建议问题
     * @param userId 用户ID
     * @param district 地区
     * @param department 部门
     * @return 建议问题列表
     */
    List<String> getSuggestedQuestions(String userId, String district, String department);
    
    /**
     * 获取可用模型列表
     * @return 模型名称和描述映射
     */
    Map<String, String> getAvailableModels();
} 