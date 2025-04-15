package cn.stylefeng.guns.modular.chat.service.impl;


import cn.stylefeng.guns.modular.chat.entity.ChatHistory;
import cn.stylefeng.guns.modular.chat.mapper.ChatHistoryMapper;
import cn.stylefeng.guns.modular.chat.service.ChatHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 聊天历史记录服务实现类
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatHistory saveChatHistory(String sessionId, String userId, String question, String answer) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setSessionId(sessionId);
        chatHistory.setUserId(userId);
        chatHistory.setQuestion(question);
        chatHistory.setAnswer(answer);
        chatHistory.setCreateTime(LocalDateTime.now());
        this.save(chatHistory);
        return chatHistory;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatHistory saveChatHistory(String sessionId, String userId, String question, String answer,
                                     String district, String department) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setSessionId(sessionId);
        chatHistory.setUserId(userId);
        chatHistory.setQuestion(question);
        chatHistory.setAnswer(answer);
        chatHistory.setDistrict(district);
        chatHistory.setDepartment(department);
        chatHistory.setCreateTime(LocalDateTime.now());
        this.save(chatHistory);
        return chatHistory;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatHistory saveChatHistory(String sessionId, String userId, String question, String answer,
                                     String keywords) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setSessionId(sessionId);
        chatHistory.setUserId(userId);
        chatHistory.setQuestion(question);
        chatHistory.setAnswer(answer);
        chatHistory.setKeywords(keywords);
        chatHistory.setCreateTime(LocalDateTime.now());
        this.save(chatHistory);
        return chatHistory;
    }

    @Override
    public List<ChatHistory> getChatHistoryBySession(String sessionId) {
        return this.baseMapper.findBySessionIdOrderByCreateTimeDesc(sessionId);
    }

    @Override
    public List<ChatHistory> getChatHistoryBySessionAsc(String sessionId) {
        return this.baseMapper.findBySessionIdOrderByCreateTimeAsc(sessionId);
    }

    @Override
    public List<ChatHistory> getChatHistoryByUser(String userId) {
        return this.baseMapper.findByUserIdOrderByCreateTimeDesc(userId);
    }

    @Override
    public List<ChatHistory> searchChatHistory(String userId, String keyword,
                                              LocalDateTime startTime, LocalDateTime endTime) {
        return this.baseMapper.searchChatHistory(userId, keyword, startTime, endTime);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteChatHistory(String sessionId) {
        return this.baseMapper.deleteBySessionId(sessionId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteChatHistoryByUser(String userId) {
        return this.baseMapper.deleteByUserId(userId) > 0;
    }

    @Override
    public String exportChatHistory(String sessionId) throws IOException {
        List<ChatHistory> histories = this.baseMapper.findBySessionIdOrderByCreateTimeAsc(sessionId);

        // 创建导出目录
        Path exportDir = Paths.get("exports");
        Files.createDirectories(exportDir);

        // 生成文件名
        String fileName = "chat_history_" + sessionId + "_" +
                         UUID.randomUUID().toString().substring(0, 8) + ".txt";
        Path filePath = exportDir.resolve(fileName);

        // 格式化对话历史
        String content = histories.stream()
                .map(history -> String.format(
                    "[%s] User: %s\nAssistant: %s\n",
                    history.getCreateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    history.getQuestion(),
                    history.getAnswer()
                ))
                .collect(Collectors.joining("\n"));

        // 写入文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(String.valueOf(filePath)))) {
            writer.write(content);
            System.out.println("内容已成功写入文件。");
        } catch (IOException e) {
            System.err.println("写入文件时发生错误: " + e.getMessage());
        }
        return filePath.toString();
    }

    @Override
    public List<ChatHistory> getRecentChats(String userId, int limit) {
        return this.baseMapper.findTopByUserIdOrderByCreateTimeDesc(userId, limit);
    }

    @Override
    public List<String> getFrequentQuestions(String userId) {
        return this.baseMapper.findFrequentQuestions(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean clearOldChats(LocalDateTime before) {
        return this.baseMapper.deleteByCreateTimeBefore(before) > 0;
    }

    @Override
    public List<String> getRelatedQuestions(String question) {
        return this.baseMapper.findRelatedQuestions(question);
    }
}
