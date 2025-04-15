package cn.stylefeng.guns.modular.chathistory.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.stylefeng.guns.modular.chathistory.entity.ChatHistory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天历史记录服务接口
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 分页查询会话历史记录
     */
    Page<ChatHistory> findBySessionId(long current, long size, String sessionId);

    /**
     * 分页查询用户历史记录
     */
    Page<ChatHistory> findByUserId(long current, long size, String userId);

    /**
     * 搜索聊天历史记录
     */
    Page<ChatHistory> searchChatHistory(
            long current,
            long size,
            String userId,
            String keyword,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * 删除会话历史记录
     */
    boolean deleteBySessionId(String sessionId);

    /**
     * 删除用户历史记录
     */
    boolean deleteByUserId(String userId);

    /**
     * 查询用户常见问题
     */
    List<String> findFrequentQuestions(String userId);

    /**
     * 查询相关问题
     */
    List<String> findRelatedQuestions(String question);

    /**
     * 创建聊天历史记录
     */
    ChatHistory createChatHistory(ChatHistory chatHistory);

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
     * 获取用户最近的聊天记录
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 聊天历史记录列表
     */
    List<ChatHistory> getRecentChats(String userId, int limit);

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
     * 获取用户频繁提问的问题
     *
     * @param userId 用户ID
     * @return 问题列表
     */
    List<String> getFrequentQuestions(String userId);

    /**
     * 获取相关问题
     *
     * @param question 问题
     * @return 相关问题列表
     */
    List<String> getRelatedQuestions(String question);

    /**
     * 导出聊天历史记录
     *
     * @param sessionId 会话ID
     * @return 导出文件路径
     * @throws IOException IO异常
     */
    String exportChatHistory(String sessionId) throws IOException;

    /**
     * 清理旧聊天记录
     *
     * @param before 时间点
     * @return 清理结果
     */
    boolean clearOldChats(LocalDateTime before);
}
