package cn.stylefeng.guns.modular.knowledge.service.impl;

import cn.stylefeng.guns.modular.kernel.service.MilvusService;
import cn.stylefeng.guns.modular.knowledge.entity.TextVector;
import cn.stylefeng.guns.modular.knowledge.service.VectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 向量服务实现类
 */
@Service
public class VectorServiceImpl implements VectorService {

    @Autowired
    private MilvusService milvusService;

    @Override
    public void addDocuments(List<TextVector> documents) {
        milvusService.insert(documents);
    }

    @Override
    public List<TextVector> searchSimilar(String query, int topK) {
        return milvusService.search(query, topK);
    }

    @Override
    public void deleteDocument(String documentId) {
        milvusService.delete(documentId);
    }

    @Override
    public void updateDocument(String documentId, TextVector document) {
        milvusService.update(documentId, document);
    }
} 