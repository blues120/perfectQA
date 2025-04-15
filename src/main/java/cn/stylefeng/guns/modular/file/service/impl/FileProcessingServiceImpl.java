package cn.stylefeng.guns.modular.file.service.impl;

import cn.stylefeng.guns.modular.document.entity.Document;
import cn.stylefeng.guns.modular.file.service.FileProcessingService;
import cn.stylefeng.guns.modular.text.service.TextPreprocessingService;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class FileProcessingServiceImpl implements FileProcessingService {

    private static final AtomicLong idGenerator = new AtomicLong(1);
    
    @Autowired
    private TextPreprocessingService textPreprocessingService;
    
    @Autowired
    private EmbeddingStore<TextSegment> embeddingStore;
    
    @Autowired
    private EmbeddingModel embeddingModel;

    @Override
    public Document processFile(MultipartFile file, String district, String department, String keywords) {
        try {
            // 读取文件内容
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            
            // 预处理文本
            String processedText = textPreprocessingService.preprocessText(content);
            
            // 创建文档对象
            Document document = new Document();
            document.setId(idGenerator.getAndIncrement());
            document.setFileName(file.getOriginalFilename());
            document.setContent(processedText);
            document.setDistrict(district);
            document.setDepartment(department);
            document.setKeywords(keywords);
            document.setCreateTime(LocalDateTime.now());
            
            // 将文本分割成段落并创建嵌入
            List<String> paragraphs = textPreprocessingService.splitIntoParagraphs(processedText);
            List<TextSegment> segments = new ArrayList<>();
            List<Embedding> embeddings = new ArrayList<>();
            
            for (String paragraph : paragraphs) {
                TextSegment segment = TextSegment.from(paragraph);
                segments.add(segment);
                embeddings.add(embeddingModel.embed(segment).content());
            }
            
            // 存储嵌入
            embeddingStore.addAll(embeddings, segments);
            
            return document;
        } catch (IOException e) {
            throw new RuntimeException("文件处理失败：" + e.getMessage(), e);
        }
    }

    @Override
    public List<TextSegment> searchSimilarSegments(String query, String district, String department) {
        // 预处理查询文本
        String processedQuery = textPreprocessingService.preprocessText(query);
        
        // 创建查询段并生成嵌入
        TextSegment querySegment = TextSegment.from(processedQuery);
        Embedding queryEmbedding = embeddingModel.embed(querySegment).content();
        
        // 搜索相似段落
        List<TextSegment> similarSegments = embeddingStore.findRelevant(queryEmbedding, 5)
            .stream()
            .map(match -> match.embedded())
            .collect(Collectors.toList());
        
        // 根据地区和部门过滤结果
        return similarSegments.stream()
                .filter(segment -> {
                    // 这里需要根据实际需求实现过滤逻辑
                    return true;
                })
                .collect(Collectors.toList());
    }
} 