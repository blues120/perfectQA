package cn.stylefeng.guns.modular.chat.service;

import java.util.List;
import java.util.Map;

public interface ArxivService {
    
    /**
     * 搜索论文
     * @param query 搜索查询
     * @return 论文列表
     */
    List<Map<String, String>> searchPapers(String query);
    
    /**
     * 获取论文详情
     * @param paperId 论文ID
     * @return 论文详情
     */
    Map<String, String> getPaperDetails(String paperId);
    
    /**
     * 获取论文摘要
     * @param paperId 论文ID
     * @return 论文摘要
     */
    String getPaperAbstract(String paperId);
    
    /**
     * 获取论文引用
     * @param paperId 论文ID
     * @return 引用列表
     */
    List<Map<String, String>> getPaperCitations(String paperId);
    
    /**
     * 获取作者信息
     * @param authorId 作者ID
     * @return 作者信息
     */
    Map<String, String> getAuthorInfo(String authorId);
} 