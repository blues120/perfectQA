package cn.stylefeng.guns.modular.knowledge.service;

import cn.stylefeng.guns.modular.knowledge.entity.KnowledgeBase;
import cn.stylefeng.guns.modular.knowledge.entity.KnowledgeDocument;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 知识库服务接口
 */
public interface KnowledgeBaseService extends IService<KnowledgeBase> {

    /**
     * 创建知识库
     */
    Boolean create(KnowledgeBase knowledgeBase);

    /**
     * 更新知识库
     */
    Boolean update(KnowledgeBase knowledgeBase);

    /**
     * 删除知识库
     */
    Boolean delete(String id);

    /**
     * 获取知识库详情
     */
    KnowledgeBase getById(String id);

    /**
     * 获取知识库列表
     */
    List<KnowledgeBase> list();

    /**
     * 元数据分类检索
     */
    List<KnowledgeBase> searchByMetadata(Map<String, Object> params);

    /**
     * 获取用户的知识库列表
     */
    List<KnowledgeBase> getUserKnowledgeBases(Long userId);

    /**
     * 设置知识库权限
     */
    void setKnowledgeBasePermission(Long knowledgeBaseId, Long userId, String permission);

    /**
     * 添加知识库标签
     */
    void addKnowledgeBaseTag(Long knowledgeBaseId, String tag);

    /**
     * 删除知识库标签
     */
    void removeKnowledgeBaseTag(Long knowledgeBaseId, String tag);

    /**
     * 删除知识库及其所有文档
     */
    void deleteKnowledgeBase(String id);

    /**
     * 获取知识库详细信息
     */
    KnowledgeBase getKnowledgeBaseDetail(String id);

    /**
     * 上传文档到知识库
     */
    KnowledgeDocument uploadDocument(String knowledgeBaseId, KnowledgeDocument document);

    /**
     * 从知识库中删除文档
     */
    void deleteDocument(String knowledgeBaseId, String documentId);

    /**
     * 获取知识库中的文档列表
     */
    List<KnowledgeDocument> getDocuments(String knowledgeBaseId);

    /**
     * 处理文档
     */
    void processDocument(String knowledgeBaseId, String documentId);

    /**
     * 搜索文档
     */
    List<KnowledgeDocument> search(String knowledgeBaseId, String query, int topK);
} 