package cn.stylefeng.guns.modular.text.service;

import java.util.List;
import java.util.Map;

public interface TextPreprocessingService {
    
    /**
     * 预处理文本
     * @param text 原始文本
     * @return 预处理后的文本
     */
    String preprocessText(String text);
    
    /**
     * 将文本分割成句子
     * @param text 原始文本
     * @return 句子列表
     */
    List<String> splitIntoSentences(String text);
    
    /**
     * 将文本分割成段落
     * @param text 原始文本
     * @return 段落列表
     */
    List<String> splitIntoParagraphs(String text);
    
    /**
     * 创建文本块
     * @param text 原始文本
     * @param chunkSize 块大小
     * @return 文本块列表
     */
    List<String> createTextChunks(String text, int chunkSize);
    
    /**
     * 提取文本元数据
     * @param text 原始文本
     * @return 元数据映射
     */
    Map<String, Object> extractMetadata(String text);
} 