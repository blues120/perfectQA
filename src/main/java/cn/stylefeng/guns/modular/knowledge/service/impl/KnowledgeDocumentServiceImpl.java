package cn.stylefeng.guns.modular.knowledge.service.impl;

import cn.stylefeng.guns.modular.knowledge.entity.KnowledgeDocument;
import cn.stylefeng.guns.modular.knowledge.entity.TextVector;
import cn.stylefeng.guns.modular.knowledge.mapper.KnowledgeDocumentMapper;
import cn.stylefeng.guns.modular.knowledge.service.KnowledgeDocumentService;
import cn.stylefeng.guns.modular.kernel.service.MilvusService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 知识文档服务实现类
 */
@Service
public class KnowledgeDocumentServiceImpl extends ServiceImpl<KnowledgeDocumentMapper, KnowledgeDocument> implements KnowledgeDocumentService {

    @Autowired
    private MilvusService milvusService;

    private final Path uploadDir = Paths.get("uploads");

    @Override
    public List<KnowledgeDocument> listByKnowledgeBaseId(Long knowledgeBaseId) {
        QueryWrapper<KnowledgeDocument> wrapper = new QueryWrapper<>();
        wrapper.eq("knowledge_base_id", knowledgeBaseId);
        return list(wrapper);
    }

    @Override
    public boolean addDocument(KnowledgeDocument document) {
        return save(document);
    }

    @Override
    public boolean updateDocument(KnowledgeDocument document) {
        return updateById(document);
    }

    @Override
    public boolean deleteDocument(Long id) {
        KnowledgeDocument document = getById(id);
        if (document != null) {
            try {
                // 删除文件
                Files.deleteIfExists(Paths.get(document.getFilePath()));
                // 删除向量
                milvusService.delete(String.valueOf(id));
                // 删除记录
                return removeById(id);
            } catch (IOException e) {
                throw new RuntimeException("删除文档失败", e);
            }
        }
        return false;
    }

    @Override
    public boolean deleteDocuments(List<Long> ids) {
        for (Long id : ids) {
            deleteDocument(id);
        }
        return true;
    }

    @Override
    public Boolean uploadDocument(MultipartFile file, String knowledgeBaseId) {
        try {
            // 创建上传目录
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + "." + extension;
            Path filePath = uploadDir.resolve(newFilename);

            // 保存文件
            Files.copy(file.getInputStream(), filePath);

            // 创建文档记录
            KnowledgeDocument document = new KnowledgeDocument();
            document.setKnowledgeBaseId(Long.parseLong(knowledgeBaseId));
            document.setOriginalName(originalFilename);
            document.setFileName(newFilename);
            document.setFileType(extension);
            document.setFilePath(filePath.toString());
            document.setSize(file.getSize());
            document.setStatus(0); // 未处理状态

            // 保存文档记录
            save(document);

            return true;
        } catch (IOException e) {
            throw new RuntimeException("上传文档失败", e);
        }
    }

    @Override
    public Boolean uploadDocuments(List<MultipartFile> files, String knowledgeBaseId) {
        for (MultipartFile file : files) {
            uploadDocument(file, knowledgeBaseId);
        }
        return true;
    }

    @Override
    public void parseDocument(Long documentId) {
        // TODO: 实现文档解析逻辑
    }

    @Override
    public void chunkDocument(Long documentId) {
        // TODO: 实现文档分块逻辑
    }

    @Override
    public void vectorizeDocument(Long documentId) {
        // TODO: 实现文档向量化逻辑
    }

    @Override
    public void createDocumentVersion(Long documentId) {
        // TODO: 实现文档版本创建逻辑
    }

    @Override
    public void restoreDocumentVersion(Long documentId, Long versionId) {
        // TODO: 实现文档版本恢复逻辑
    }

    @Override
    public List<KnowledgeDocument> getDocumentVersions(Long documentId) {
        // TODO: 实现获取文档版本列表逻辑
        return null;
    }

    @Override
    public void setDocumentMetadata(Long documentId, String key, String value) {
        // TODO: 实现设置文档元数据逻辑
    }

    @Override
    public String getDocumentMetadata(Long documentId, String key) {
        // TODO: 实现获取文档元数据逻辑
        return null;
    }

    @Override
    public List<KnowledgeDocument> searchByKeyword(Map<String, Object> params) {
        String keyword = (String) params.get("keyword");
        String knowledgeBaseId = (String) params.get("knowledgeBaseId");

        // 使用向量搜索
        List<TextVector> vectors = milvusService.search(keyword, 10);

        QueryWrapper<KnowledgeDocument> wrapper = new QueryWrapper<>();
        wrapper.in("id", vectors.stream().map(TextVector::getId).collect(Collectors.toList()));
        if (knowledgeBaseId != null) {
            wrapper.eq("knowledge_base_id", knowledgeBaseId);
        }
        return list(wrapper);
    }

    @Override
    public String getDocumentPreview(String id) {
        KnowledgeDocument document = getById(id);
        if (document != null) {
            return document.getContent();
        }
        return null;
    }
} 