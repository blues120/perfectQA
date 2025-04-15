package cn.stylefeng.guns.modular.knowledge.service;

import cn.stylefeng.guns.modular.knowledge.entity.KnowledgeDocument;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 知识文档服务接口
 */
public interface KnowledgeDocumentService extends IService<KnowledgeDocument> {

    /**
     * 根据知识库ID获取文档列表
     *
     * @param knowledgeBaseId 知识库ID
     * @return 文档列表
     */
    List<KnowledgeDocument> listByKnowledgeBaseId(Long knowledgeBaseId);

    /**
     * 添加文档
     *
     * @param document 文档信息
     * @return 是否成功
     */
    boolean addDocument(KnowledgeDocument document);

    /**
     * 更新文档
     *
     * @param document 文档信息
     * @return 是否成功
     */
    boolean updateDocument(KnowledgeDocument document);

    /**
     * 删除文档
     *
     * @param id 文档ID
     * @return 是否成功
     */
    boolean deleteDocument(Long id);

    /**
     * 批量删除文档
     *
     * @param ids 文档ID列表
     * @return 是否成功
     */
    boolean deleteDocuments(List<Long> ids);

    /**
     * 上传文档
     */
    Boolean uploadDocument(MultipartFile file, String knowledgeBaseId);

    /**
     * 批量上传文档
     */
    Boolean uploadDocuments(List<MultipartFile> files, String knowledgeBaseId);

    /**
     * 解析文档内容
     */
    void parseDocument(Long documentId);

    /**
     * 分块处理文档
     */
    void chunkDocument(Long documentId);

    /**
     * 向量化文档
     */
    void vectorizeDocument(Long documentId);

    /**
     * 创建文档版本
     */
    void createDocumentVersion(Long documentId);

    /**
     * 恢复文档版本
     */
    void restoreDocumentVersion(Long documentId, Long versionId);

    /**
     * 获取文档版本列表
     */
    List<KnowledgeDocument> getDocumentVersions(Long documentId);

    /**
     * 设置文档元数据
     */
    void setDocumentMetadata(Long documentId, String key, String value);

    /**
     * 获取文档元数据
     */
    String getDocumentMetadata(Long documentId, String key);

    /**
     * 关键词搜索
     */
    List<KnowledgeDocument> searchByKeyword(Map<String, Object> params);

    /**
     * 获取文档预览
     */
    String getDocumentPreview(String id);
} 