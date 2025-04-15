package cn.stylefeng.guns.modular.chat.service.impl;

import cn.stylefeng.guns.modular.chat.entity.ChatMessage;
import cn.stylefeng.guns.modular.chat.entity.ChatSession;
import cn.stylefeng.guns.modular.chat.mapper.ChatMessageMapper;
import cn.stylefeng.guns.modular.chat.mapper.ChatSessionMapper;
import cn.stylefeng.guns.modular.chat.service.*;
import cn.stylefeng.guns.modular.kernel.service.VectorService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl extends ServiceImpl<ChatSessionMapper, ChatSession> implements ChatService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private VectorService vectorService;

    @Autowired
    private SessionNotificationService sessionNotificationService;

    @Autowired
    private LLMService llmService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private MultimodalService multimodalService;

    @Autowired
    private ArxivService arxivService;

    @Autowired
    private WolframService wolframService;

    @Value("${chat.file.upload.path:uploads/chat}")
    private String uploadPath;

    @Value("${chat.file.max.size:10485760}")
    private long maxFileSize;

    @Value("${chat.file.allowed.types:pdf,doc,docx,txt,jpg,jpeg,png}")
    private String allowedFileTypes;

    @Value("${chat.session.timeout.minutes:30}")
    private int sessionTimeoutMinutes;

    @Value("${chat.session.cleanup.days:7}")
    private int sessionCleanupDays;

    @Override
    public ChatSession createSession(String userId, String type) {
        ChatSession session = new ChatSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUserId(userId);
        session.setType(type);
        session.setStatus("active");
        session.setLastActivityTime(LocalDateTime.now());
        save(session);

        // 发送会话创建通知
        sessionNotificationService.sendSessionCreateNotification(session);

        return session;
    }

    @Override
    public ChatMessage sendMessage(Long sessionId, String content, String type) {
        ChatSession session = getById(sessionId);
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }

        // 检查会话状态
        if (!"active".equals(session.getStatus())) {
            throw new RuntimeException("会话已关闭或超时");
        }

        // 更新最后活动时间
        session.setLastActivityTime(LocalDateTime.now());
        updateById(session);

        // 创建用户消息
        ChatMessage userMessage = new ChatMessage();
        userMessage.setSessionId(sessionId);
        userMessage.setRole("user");
        userMessage.setContent(content);
        userMessage.setType(type);
        chatMessageMapper.insert(userMessage);

        // 根据会话类型处理消息
        String response = processMessage(session, content);

        // 创建助手消息
        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setSessionId(sessionId);
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(response);
        assistantMessage.setType("text");
        chatMessageMapper.insert(assistantMessage);

        return assistantMessage;
    }

    /**
     * 检查会话超时
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60000)
    public void checkSessionTimeout() {
        LocalDateTime timeoutThreshold = LocalDateTime.now().minus(sessionTimeoutMinutes, ChronoUnit.MINUTES);
        QueryWrapper<ChatSession> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "active")
                .lt("last_activity_time", timeoutThreshold);

        List<ChatSession> timeoutSessions = list(wrapper);
        for (ChatSession session : timeoutSessions) {
            // 发送超时通知
            sessionNotificationService.sendSessionTimeoutNotification(session);
            closeSession(session.getId());
        }
    }

    /**
     * 清理过期会话
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredSessions() {
        LocalDateTime cleanupThreshold = LocalDateTime.now().minus(sessionCleanupDays, ChronoUnit.DAYS);
        QueryWrapper<ChatSession> wrapper = new QueryWrapper<>();
        wrapper.lt("create_time", cleanupThreshold);

        List<ChatSession> expiredSessions = list(wrapper);
        for (ChatSession session : expiredSessions) {
            // 删除会话相关的消息
            QueryWrapper<ChatMessage> messageWrapper = new QueryWrapper<>();
            messageWrapper.eq("session_id", session.getId());
            chatMessageMapper.delete(messageWrapper);

            // 删除会话
            removeById(session.getId());
        }
    }

    private String processMessage(ChatSession session, String content) {
        switch (session.getType()) {
            case "LLM":
                return processLLMMessage(session, content);
            case "知识库":
                return processKnowledgeMessage(content);
            case "搜索引擎":
                return processSearchMessage(content);
            case "数据库":
                return processDatabaseMessage(content);
            case "多模态":
                return processMultimodalMessage(content);
            case "ARXIV":
                return processArxivMessage(content);
            case "Wolfram":
                return processWolframMessage(content);
            default:
                return "不支持的会话类型";
        }
    }

    private String processLLMMessage(ChatSession session, String content) {
        // 获取历史消息
        List<ChatMessage> history = getSessionHistory(session.getId());
        List<String> messages = history.stream()
                .map(ChatMessage::getContent)
                .collect(Collectors.toList());

        // 使用LLM服务处理消息
        return llmService.processConversation(messages, content);
    }

    private String processKnowledgeMessage(String content) {
        // 使用向量服务搜索相似内容
        List<String> similarTexts = vectorService.searchSimilarTexts(content, 3);
        return String.join("\n", similarTexts);
    }

    private String processSearchMessage(String content) {
        // 使用搜索引擎服务
        List<Map<String, Object>> searchResults = searchService.search(content);
        return String.join("\n", (CharSequence) searchResults);
    }

    private String processDatabaseMessage(String content) {
        // 验证SQL语句
        if (!databaseService.validateSql(content)) {
            return "无效的SQL语句";
        }

        // 执行SQL查询
        List<Map<String, Object>> results = databaseService.executeQuery(content);
        return formatDatabaseResults(results);
    }

    private String processMultimodalMessage(String content) {
        // 根据内容类型选择处理方法
        if (content.startsWith("image:")) {
            String imagePath = content.substring(6);
            return multimodalService.processImage((MultipartFile) new File(imagePath));
        } else if (content.startsWith("audio:")) {
            String audioPath = content.substring(6);
            return multimodalService.processAudio((MultipartFile) new File(audioPath));
        } else if (content.startsWith("video:")) {
            String videoPath = content.substring(6);
            return multimodalService.processVideo((MultipartFile) new File(videoPath));
        }
        return "不支持的多模态内容类型";
    }

    private String processArxivMessage(String content) {
        // 搜索论文
        List<Map<String, String>> papers = arxivService.searchPapers(content);
        return formatArxivResults(papers);
    }

    private String processWolframMessage(String content) {
        // 执行数学计算
        return wolframService.calculate(content);
    }

    private String formatDatabaseResults(List<Map<String, Object>> results) {
        if (results.isEmpty()) {
            return "查询结果为空";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("查询结果：\n");
        for (Map<String, Object> row : results) {
            sb.append("-------------------\n");
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }
        return sb.toString();
    }

    private String formatArxivResults(List<Map<String, String>> papers) {
        if (papers.isEmpty()) {
            return "未找到相关论文";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("搜索结果：\n");
        for (Map<String, String> paper : papers) {
            sb.append("-------------------\n");
            sb.append("标题: ").append(paper.get("title")).append("\n");
            sb.append("作者: ").append(paper.get("authors")).append("\n");
            sb.append("摘要: ").append(paper.get("abstract")).append("\n");
            sb.append("链接: ").append(paper.get("url")).append("\n");
        }
        return sb.toString();
    }

    @Override
    public ChatMessage sendFileMessage(Long sessionId, MultipartFile file) {
        try {
            // 验证文件大小
            if (file.getSize() > maxFileSize) {
                throw new RuntimeException("文件大小超过限制");
            }

            // 验证文件类型
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            if (!allowedFileTypes.contains(fileExtension.toLowerCase())) {
                throw new RuntimeException("不支持的文件类型");
            }

            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + "." + fileExtension;
            String filePath = uploadPath + "/" + fileName;

            // 确保目录存在
            File dest = new File(filePath);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }

            // 保存文件
            file.transferTo(dest);

            // 创建文件消息
            ChatMessage message = new ChatMessage();
            message.setSessionId(sessionId);
            message.setRole("user");
            message.setContent(originalFilename);
            message.setType("file");
            message.setFilePath(filePath);
            chatMessageMapper.insert(message);

            return message;
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ChatMessage> getSessionHistory(Long sessionId) {
        QueryWrapper<ChatMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("session_id", sessionId)
                .orderByAsc("create_time");
        return chatMessageMapper.selectList(wrapper);
    }

    @Override
    public void closeSession(Long sessionId) {
        ChatSession session = getById(sessionId);
        if (session != null) {
            session.setStatus("closed");
            session.setUpdateTime(LocalDateTime.now());
            updateById(session);

            // 发送会话关闭通知
            sessionNotificationService.sendSessionCloseNotification(session);
        }
    }

    @Override
    public List<ChatSession> getUserSessions(String userId) {
        QueryWrapper<ChatSession> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .orderByDesc("update_time");
        return list(wrapper);
    }
}
