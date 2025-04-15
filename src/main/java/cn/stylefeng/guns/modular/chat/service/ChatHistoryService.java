package cn.stylefeng.guns.modular.chat.service;


import cn.stylefeng.guns.modular.chat.entity.ChatHistory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天历史记录服务接口
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 创建聊天历史记录
     *
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @param question 问题
     * @param answer 回答
     * @return 创建结果
     */
    ChatHistory saveChatHistory(String sessionId, String userId, String question, String answer);

    /**
     * 创建聊天历史记录（带区域和部门信息）
     *
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @param question 问题
     * @param answer 回答
     * @param district 区域
     * @param department 部门
     * @return 创建结果
     */
    ChatHistory saveChatHistory(String sessionId, String userId, String question, String answer,
                              String district, String department);

    /**
     * 创建聊天历史记录（带关键词）
     *
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @param question 问题
     * @param answer 回答
     * @param keywords 关键词
     * @return 创建结果
     */
    ChatHistory saveChatHistory(String sessionId, String userId, String question, String answer,
                              String keywords);

    /**
     * 获取会话的聊天历史记录
     *
     * @param sessionId 会话ID
     * @return 聊天历史记录列表
     */
    List<ChatHistory> getChatHistoryBySession(String sessionId);

    /**
     * 获取会话的聊天历史记录（按时间升序）
     *
     * @param sessionId 会话ID
     * @return 聊天历史记录列表
     */
    List<ChatHistory> getChatHistoryBySessionAsc(String sessionId);

    /**
     * 获取用户的聊天历史记录
     *
     * @param userId 用户ID
     * @return 聊天历史记录列表
     */
    List<ChatHistory> getChatHistoryByUser(String userId);

    /**
     * 搜索聊天历史记录
     *
     * @param userId 用户ID
     * @param keyword 关键词
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 聊天历史记录列表
     */
    List<ChatHistory> searchChatHistory(String userId, String keyword,
                                       LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 删除会话的聊天历史记录
     *
     * @param sessionId 会话ID
     * @return 删除结果
     */
    boolean deleteChatHistory(String sessionId);

    /**
     * 删除用户的聊天历史记录
     *
     * @param userId 用户ID
     * @return 删除结果
     */
    boolean deleteChatHistoryByUser(String userId);

    /**
     * 导出聊天历史记录
     *
     * @param sessionId 会话ID
     * @return 文件路径
     * @throws java.io.IOException IO异常
     */
    String exportChatHistory(String sessionId) throws java.io.IOException;

    /**
     * 获取用户最近的聊天记录
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 聊天历史记录列表
     */
    List<ChatHistory> getRecentChats(String userId, int limit);

    /**
     * 获取用户常用问题
     *
     * @param userId 用户ID
     * @return 问题列表
     */
    List<String> getFrequentQuestions(String userId);

    /**
     * 清理旧聊天记录
     *
     * @param before 时间点
     * @return 删除结果
     */
    boolean clearOldChats(LocalDateTime before);

    /**
     * 获取相关问题
     *
     * @param question 问题
     * @return 相关问题列表
     */
    List<String> getRelatedQuestions(String question);
}
