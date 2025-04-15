package cn.stylefeng.guns.modular.chat.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * LLM服务接口
 * 提供与大语言模型交互的功能
 */
public interface LLMService {
    
    /**
     * 处理单条消息
     * @param message 用户消息
     * @return 模型响应
     */
    String processMessage(String message);
    
    /**
     * 处理带上下文的消息
     * @param message 用户消息
     * @param context 上下文信息
     * @return 模型响应
     */
    String processMessageWithContext(String message, String context);
    
    /**
     * 处理对话历史
     * @param conversation 对话历史
     * @param currentMessage 当前消息
     * @return 模型响应
     */
    String processConversation(List<String> conversation, String currentMessage);
    
    /**
     * 生成响应
     * @param prompt 提示词
     * @return 模型响应
     */
    String generateResponse(String prompt);
    
    /**
     * 异步生成响应
     * @param prompt 提示词
     * @return 异步响应结果
     */
    CompletableFuture<String> generateResponseAsync(String prompt);
    
    /**
     * 生成多个响应
     * @param prompt 提示词
     * @param count 生成数量
     * @return 响应列表
     */
    List<String> generateMultipleResponses(String prompt, int count);
    
    /**
     * 分析文本
     * @param text 待分析文本
     * @param analysisType 分析类型
     * @return 分析结果
     */
    Map<String, Object> analyzeText(String text, String analysisType);
    
    /**
     * 文本摘要
     * @param text 待摘要文本
     * @param maxLength 最大长度
     * @return 摘要结果
     */
    String summarizeText(String text, int maxLength);
    
    /**
     * 文本翻译
     * @param text 待翻译文本
     * @param targetLanguage 目标语言
     * @return 翻译结果
     */
    String translateText(String text, String targetLanguage);
} 