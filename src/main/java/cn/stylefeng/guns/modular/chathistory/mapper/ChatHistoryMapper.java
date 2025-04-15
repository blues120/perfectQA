package cn.stylefeng.guns.modular.chathistory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.stylefeng.guns.modular.chathistory.entity.ChatHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天历史记录Mapper接口
 */
@Mapper
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {

    /**
     * 根据会话ID查询聊天历史记录（按时间降序）
     *
     * @param sessionId 会话ID
     * @return 聊天历史记录列表
     */
    List<ChatHistory> findBySessionIdOrderByCreateTimeDesc(@Param("sessionId") String sessionId);

    /**
     * 根据会话ID查询聊天历史记录（按时间升序）
     *
     * @param sessionId 会话ID
     * @return 聊天历史记录列表
     */
    List<ChatHistory> findBySessionIdOrderByCreateTimeAsc(@Param("sessionId") String sessionId);

    /**
     * 根据用户ID查询聊天历史记录
     *
     * @param userId 用户ID
     * @return 聊天历史记录列表
     */
    List<ChatHistory> findByUserIdOrderByCreateTimeDesc(@Param("userId") String userId);

    /**
     * 根据用户ID获取最近的聊天记录
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 聊天历史记录列表
     */
    List<ChatHistory> findTopByUserIdOrderByCreateTimeDesc(@Param("userId") String userId, @Param("limit") int limit);

    /**
     * 搜索聊天历史记录
     *
     * @param userId 用户ID
     * @param keyword 关键词
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 聊天历史记录列表
     */
    List<ChatHistory> searchChatHistory(
            @Param("userId") String userId,
            @Param("keyword") String keyword,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 根据会话ID删除聊天历史记录
     *
     * @param sessionId 会话ID
     * @return 影响行数
     */
    int deleteBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据用户ID删除聊天历史记录
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") String userId);

    /**
     * 获取用户频繁提问的问题
     *
     * @param userId 用户ID
     * @return 问题列表
     */
    List<String> findFrequentQuestions(@Param("userId") String userId);

    /**
     * 获取相关问题
     *
     * @param question 问题
     * @return 相关问题列表
     */
    List<String> findRelatedQuestions(@Param("question") String question);
}
