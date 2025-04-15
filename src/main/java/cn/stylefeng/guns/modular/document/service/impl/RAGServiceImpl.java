package cn.stylefeng.guns.modular.document.service.impl;

import cn.stylefeng.guns.modular.document.entity.Document;
import cn.stylefeng.guns.modular.document.mapper.DocumentMapper;
import cn.stylefeng.guns.modular.document.service.RAGService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.filter.MetadataFilter;
import dev.langchain4j.store.embedding.filter.builder.MetadataFilterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RAGServiceImpl extends ServiceImpl<DocumentMapper, Document> implements RAGService {
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;
    private final DocumentSplitter documentSplitter;

    @Autowired
    public RAGServiceImpl(EmbeddingStore<TextSegment> embeddingStore) {
        this.embeddingStore = embeddingStore;
        this.embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama2")
                .build();
        this.documentSplitter = DocumentSplitters.recursive(500, 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Document processDocument(MultipartFile file, String district, String department) throws IOException {
        // 保存文件
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get("uploads", fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        // 创建文档记录
        Document document = new Document();
        document.setFileName(fileName);
        document.setFileId(UUID.randomUUID().toString());
        document.setContentType(file.getContentType());
        document.setFilePath(filePath.toString());
        document.setDistrict(district);
        document.setDepartment(department);
        document.setCreateTime(LocalDateTime.now());
        document.setUpdateTime(LocalDateTime.now());
        document.setCreateUser("system");
        document.setFileSize(file.getSize());

        // 保存到数据库
        save(document);

        // 处理文档内容
        String content = extractContent(file);
        document.setContent(content);

        // 更新数据库
        updateById(document);

        // 向量化并存储
        vectorizeAndStore(document);

        return document;
    }

    private String extractContent(MultipartFile file) throws IOException {
        // TODO: 根据文件类型提取内容
        // 这里需要实现PDF、Word、TXT等文件的解析
        return new String(file.getBytes());
    }

    private void vectorizeAndStore(Document document) {
        // 分割文档
        dev.langchain4j.data.document.Document langchainDoc = 
            dev.langchain4j.data.document.Document.from(document.getContent());
        List<TextSegment> segments = documentSplitter.split(langchainDoc);

        // 创建元数据过滤器
        MetadataFilter filter = new MetadataFilterBuilder()
                .add("document_id", document.getId().toString())
                .add("district", document.getDistrict())
                .add("department", document.getDepartment())
                .build();

        // 向量化并存储
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .metadataFilter(filter)
                .build();
        ingestor.ingest(segments);
    }

    @Override
    public List<Document> searchDocuments(String query, String district, String department) {
        // 创建元数据过滤器
        MetadataFilter filter = new MetadataFilterBuilder()
                .add("district", district)
                .add("department", department)
                .build();

        // 搜索相似文档片段
        List<TextSegment> relevantSegments = embeddingStore.findRelevant(query, 5, filter);

        // 获取相关文档
        return relevantSegments.stream()
                .map(segment -> getById(Long.parseLong(segment.metadata().get("document_id"))))
                .filter(doc -> doc != null)
                .distinct()
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDocument(Long id) {
        Document document = getById(id);
        if (document != null) {
            // 删除文件
            try {
                Files.deleteIfExists(Paths.get(document.getFilePath()));
            } catch (IOException e) {
                // 记录错误但继续执行
                e.printStackTrace();
            }
            // 删除数据库记录
            removeById(id);
            // TODO: 从向量存储中删除相关向量
        }
    }
} 