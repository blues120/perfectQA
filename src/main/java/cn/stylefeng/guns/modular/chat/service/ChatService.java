package cn.stylefeng.guns.modular.chat.service;

import cn.stylefeng.guns.modular.chat.entity.ChatMessage;
import cn.stylefeng.guns.modular.chat.entity.ChatSession;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ChatService extends IService<ChatSession> {
    
    /**
     * 创建会话
     */
    ChatSession createSession(String userId, String type);
    
    /**
     * 发送消息
     */
    ChatMessage sendMessage(Long sessionId, String content, String type);
    
    /**
     * 发送文件消息
     */
    ChatMessage sendFileMessage(Long sessionId, MultipartFile file);
    
    /**
     * 获取会话历史消息
     */
    List<ChatMessage> getSessionHistory(Long sessionId);
    
    /**
     * 关闭会话
     */
    void closeSession(Long sessionId);
    
    /**
     * 获取用户的所有会话
     */
    List<ChatSession> getUserSessions(String userId);
} 