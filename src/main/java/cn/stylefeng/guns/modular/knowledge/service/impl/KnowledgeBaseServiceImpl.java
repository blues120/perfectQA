package cn.stylefeng.guns.modular.knowledge.service.impl;

import cn.stylefeng.guns.modular.kernel.service.MilvusService;
import cn.stylefeng.guns.modular.knowledge.entity.KnowledgeBase;
import cn.stylefeng.guns.modular.knowledge.entity.KnowledgeDocument;
import cn.stylefeng.guns.modular.knowledge.entity.TextVector;
import cn.stylefeng.guns.modular.knowledge.mapper.KnowledgeBaseMapper;
import cn.stylefeng.guns.modular.knowledge.mapper.KnowledgeDocumentMapper;
import cn.stylefeng.guns.modular.knowledge.service.KnowledgeBaseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 知识库服务实现类
 */
@Service
public class KnowledgeBaseServiceImpl extends ServiceImpl<KnowledgeBaseMapper, KnowledgeBase> implements KnowledgeBaseService {

    @Autowired
    private KnowledgeDocumentMapper documentMapper;
    
    @Autowired
    private MilvusService milvusService;
    
    @Override
    public Boolean create(KnowledgeBase knowledgeBase) {
        // 设置默认值
        knowledgeBase.setStatus(1);
        knowledgeBase.setDimension(384); // 使用MiniLM-L6-v2的向量维度
        return save(knowledgeBase);
    }

    @Override
    public Boolean update(KnowledgeBase knowledgeBase) {
        return updateById(knowledgeBase);
    }

    @Override
    public Boolean delete(String id) {
        return removeById(id);
    }

    @Override
    public KnowledgeBase getById(String id) {
        return super.getById(id);
    }

    @Override
    public List<KnowledgeBase> list() {
        return list(new QueryWrapper<>());
    }

    @Override
    public List<KnowledgeBase> searchByMetadata(Map<String, Object> params) {
        QueryWrapper<KnowledgeBase> wrapper = new QueryWrapper<>();
        // 根据元数据参数构建查询条件
        params.forEach((key, value) -> {
            if (value != null) {
                wrapper.eq(key, value);
            }
        });
        return list(wrapper);
    }
    
    @Override
    public List<KnowledgeBase> getUserKnowledgeBases(Long userId) {
        QueryWrapper<KnowledgeBase> wrapper = new QueryWrapper<>();
        wrapper.eq("create_user", userId);
        return list(wrapper);
    }
    
    @Override
    public void setKnowledgeBasePermission(Long knowledgeBaseId, Long userId, String permission) {
        KnowledgeBase knowledgeBase = getById(knowledgeBaseId);
        if (knowledgeBase != null) {
            knowledgeBase.setPermissions(permission);
            updateById(knowledgeBase);
        }
    }
    
    @Override
    public void addKnowledgeBaseTag(Long knowledgeBaseId, String tag) {
        KnowledgeBase knowledgeBase = getById(knowledgeBaseId);
        if (knowledgeBase != null) {
            String tags = knowledgeBase.getTags();
            if (tags == null || tags.isEmpty()) {
                tags = tag;
            } else {
                tags += "," + tag;
            }
            knowledgeBase.setTags(tags);
            updateById(knowledgeBase);
        }
    }
    
    @Override
    public void removeKnowledgeBaseTag(Long knowledgeBaseId, String tag) {
        KnowledgeBase knowledgeBase = getById(knowledgeBaseId);
        if (knowledgeBase != null) {
            String tags = knowledgeBase.getTags();
            if (tags != null && !tags.isEmpty()) {
                String[] tagArray = tags.split(",");
                StringBuilder newTags = new StringBuilder();
                for (String t : tagArray) {
                    if (!t.equals(tag)) {
                        if (newTags.length() > 0) {
                            newTags.append(",");
                        }
                        newTags.append(t);
                    }
                }
                knowledgeBase.setTags(newTags.toString());
                updateById(knowledgeBase);
            }
        }
    }
    
    @Override
    @Transactional
    public void deleteKnowledgeBase(String id) {
        // 删除知识库
        removeById(id);
        
        // 删除知识库中的文档
        QueryWrapper<KnowledgeDocument> wrapper = new QueryWrapper<>();
        wrapper.eq("knowledge_base_id", id);
        List<KnowledgeDocument> documents = documentMapper.selectList(wrapper);
        for (KnowledgeDocument document : documents) {
            // 删除文档文件
            documentMapper.deleteById(document.getId());
            // 删除向量
            milvusService.delete(String.valueOf(document.getId()));
        }
    }
    
    @Override
    public KnowledgeBase getKnowledgeBaseDetail(String id) {
        return getById(id);
    }
    
    @Override
    @Transactional
    public KnowledgeDocument uploadDocument(String knowledgeBaseId, KnowledgeDocument document) {
        // 设置文档信息
        document.setKnowledgeBaseId(Long.parseLong(knowledgeBaseId));
        document.setStatus(0); // 未处理状态
        
        // 保存文档
        documentMapper.insert(document);
        
        return document;
    }
    
    @Override
    @Transactional
    public void deleteDocument(String knowledgeBaseId, String documentId) {
        // 删除文档
        documentMapper.deleteById(documentId);
        
        // 删除向量
        milvusService.delete(documentId);
    }
    
    @Override
    public List<KnowledgeDocument> getDocuments(String knowledgeBaseId) {
        QueryWrapper<KnowledgeDocument> wrapper = new QueryWrapper<>();
        wrapper.eq("knowledge_base_id", knowledgeBaseId);
        return documentMapper.selectList(wrapper);
    }
    
    @Override
    @Transactional
    public void processDocument(String knowledgeBaseId, String documentId) {
        // 获取文档
        KnowledgeDocument document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new RuntimeException("Document not found");
        }
        
        // 更新文档状态为处理中
        document.setStatus(1);
        documentMapper.updateById(document);
        
        try {
            // 实现文档处理逻辑
            // 1. 读取文档内容
            String content = DocumentReader.readDocument(document.getFilePath());
            // 2. 分块处理
            List<String> chunks = DocumentChunker.chunkContent(content);
            // 3. 向量化
            List<TextVector> vectors = Vectorizer.vectorizeChunks(chunks);
            // 4. 存储到向量数据库
            milvusService.insertVectors(vectors);
            
            // 更新文档状态为处理完成
            document.setStatus(2);
            document.setProgress(100);
            documentMapper.updateById(document);
        } catch (Exception e) {
            // 更新文档状态为处理失败
            document.setStatus(3);
            document.setResult(e.getMessage());
            documentMapper.updateById(document);
            throw new RuntimeException("Document processing failed", e);
        }
    }
    
    @Override
    public List<KnowledgeDocument> search(String knowledgeBaseId, String query, int topK) {
        // 搜索相似向量
        List<TextVector> vectors = milvusService.search(query, topK);
        
        // 获取文档ID列表
        List<String> documentIds = vectors.stream()
                .map(TextVector::getId)
                .collect(Collectors.toList());
        
        // 查询文档信息
        return documentMapper.selectBatchIds(documentIds);
    }
}