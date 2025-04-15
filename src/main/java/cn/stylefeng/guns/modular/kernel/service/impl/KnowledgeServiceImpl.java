package cn.stylefeng.guns.modular.kernel.service.impl;


import cn.stylefeng.guns.modular.kernel.entity.TextVector;

import cn.stylefeng.guns.modular.kernel.service.VectorService;
import cn.stylefeng.guns.modular.knowledge.entity.KnowledgeDocument;
import cn.stylefeng.guns.modular.knowledge.mapper.KnowledgeDocumentMapper;
import cn.stylefeng.guns.modular.knowledge.mapper.TextVectorMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeDocumentMapper, KnowledgeDocument> implements KnowledgeService {

    @Autowired
    private TextVectorMapper textVectorMapper;

    @Autowired
    private VectorService vectorService;

    private static final String COLLECTION_NAME = "knowledge_vectors";

    @Override
    public KnowledgeDocument uploadDocument(MultipartFile file) {
        try {
            // 保存文件
            String fileName = file.getOriginalFilename();
            String filePath = "uploads/" + fileName;
            File dest = new File(filePath);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);

            // 创建文档记录
            KnowledgeDocument document = new KnowledgeDocument();
            document.setTitle(fileName);
            document.setFilePath(filePath);
            document.setFileType(file.getContentType());
            document.setContent(new String(file.getBytes()));
            save(document);

            // 处理文档内容
            List<String> texts = splitText(document.getContent());
            vectorizeText(texts, Long.valueOf(document.getId()));

            return document;
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public List<String> splitText(String content) {
        // 简单的按段落分割
        List<String> texts = new ArrayList<>();
        String[] paragraphs = content.split("\n\n");
        for (String paragraph : paragraphs) {
            if (paragraph.trim().length() > 0) {
                texts.add(paragraph.trim());
            }
        }
        return texts;
    }

    @Override
    public List<TextVector> vectorizeText(List<String> texts, Long documentId) {
        List<TextVector> vectors = new ArrayList<>();
        List<float[]> vectorList = vectorService.vectorizeBatch(texts);

        for (int i = 0; i < texts.size(); i++) {
            TextVector vector = new TextVector();
            vector.setDocumentId(documentId);
            vector.setContent(texts.get(i));
            vector.setVector(vectorList.get(i).toString());

            // 保存到Milvus
            vectorService.saveVector(COLLECTION_NAME, vectorList.get(i), vector.getId().toString());

            textVectorMapper.insert(vector);
            vectors.add(vector);
        }
        return vectors;
    }

    @Override
    public List<TextVector> searchSimilar(String query, int topK) {
        // 向量化查询文本
        float[] queryVector = vectorService.vectorize(query);

        // 搜索相似向量
        List<String> similarIds = vectorService.searchSimilar(COLLECTION_NAME, queryVector, topK);

        // 获取对应的文本向量
        List<TextVector> results = new ArrayList<>();
        for (String id : similarIds) {
            TextVector vector = textVectorMapper.selectById(Long.parseLong(id));
            if (vector != null) {
                results.add(vector);
            }
        }
        return results;
    }

    @Override
    public String getDocumentContent(Long documentId) {
        KnowledgeDocument document = getById(documentId);
        return document != null ? document.getContent() : null;
    }

    @Override
    public void deleteDocument(Long documentId) {
        // 删除文件
        KnowledgeDocument document = getById(documentId);
        if (document != null) {
            File file = new File(document.getFilePath());
            if (file.exists()) {
                file.delete();
            }
        }
        // 删除记录
        removeById(documentId);
    }
}
