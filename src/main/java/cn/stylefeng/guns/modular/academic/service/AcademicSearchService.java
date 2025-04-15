package cn.stylefeng.guns.modular.academic.service;

import java.util.List;
import java.util.Map;

/**
 * 学术搜索服务接口
 */
public interface AcademicSearchService {
    
    /**
     * 搜索学术论文
     * @param query 搜索查询
     * @param filters 过滤条件
     * @return 搜索结果
     */
    List<Map<String, Object>> searchPapers(String query, Map<String, Object> filters);
    
    /**
     * 获取论文详情
     * @param paperId 论文ID
     * @return 论文详情
     */
    Map<String, Object> getPaperDetails(String paperId);
    
    /**
     * 获取相关论文
     * @param paperId 论文ID
     * @return 相关论文列表
     */
    List<Map<String, Object>> getRelatedPapers(String paperId);
    
    /**
     * 获取作者信息
     * @param authorId 作者ID
     * @return 作者信息
     */
    Map<String, Object> getAuthorInfo(String authorId);
    
    /**
     * 获取作者发表的论文
     * @param authorId 作者ID
     * @return 论文列表
     */
    List<Map<String, Object>> getAuthorPapers(String authorId);
    
    /**
     * 获取期刊信息
     * @param journalId 期刊ID
     * @return 期刊信息
     */
    Map<String, Object> getJournalInfo(String journalId);
    
    /**
     * 获取期刊论文
     * @param journalId 期刊ID
     * @return 论文列表
     */
    List<Map<String, Object>> getJournalPapers(String journalId);
} 