package cn.stylefeng.guns.modular.chat.service;

import cn.stylefeng.guns.modular.chat.entity.ChatSession;

public interface SessionNotificationService {
    
    /**
     * 发送会话关闭通知
     * @param session 关闭的会话
     */
    void sendSessionCloseNotification(ChatSession session);
    
    /**
     * 发送会话超时通知
     * @param session 超时的会话
     */
    void sendSessionTimeoutNotification(ChatSession session);
    
    /**
     * 发送会话创建通知
     * @param session 新创建的会话
     */
    void sendSessionCreateNotification(ChatSession session);
} 