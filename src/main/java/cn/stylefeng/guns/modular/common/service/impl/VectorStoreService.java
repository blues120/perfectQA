package cn.stylefeng.guns.modular.common.service.impl;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VectorStoreService {
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    public VectorStoreService() {
        // 初始化Ollama嵌入模型
        this.embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama2")
                .build();

        // 初始化Chroma向量存储
        this.embeddingStore = ChromaEmbeddingStore.builder()
                .baseUrl("http://localhost:8000")
                .collectionName("documents")
                .build();
    }

    public void addDocuments(List<Document> documents) {
        // 文档分块
        DocumentSplitter splitter = DocumentSplitters.recursive(500, 0);
        List<TextSegment> segments = splitter.splitAll(documents);

        // 生成嵌入向量并存储
        embeddingStore.addAll(segments, embeddingModel);
    }

    public List<TextSegment> findRelevant(String query, int maxResults) {
        return embeddingStore.findRelevant(query, maxResults);
    }

    // 添加getter方法
    public EmbeddingStore<TextSegment> getEmbeddingStore() {
        return embeddingStore;
    }

    public EmbeddingModel getEmbeddingModel() {
        return embeddingModel;
    }
}
