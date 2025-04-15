package cn.stylefeng.guns.modular.kernel.service.impl;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.modality.nlp.qa.QAInput;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import cn.stylefeng.guns.modular.kernel.service.MilvusService;
import cn.stylefeng.guns.modular.knowledge.entity.TextVector;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Milvus向量数据库服务实现类
 */
@Service
public class MilvusServiceImpl implements MilvusService {

    private static final String COLLECTION_NAME = "text_vectors";
    private static final String MODEL_NAME = "sentence-transformers/all-MiniLM-L6-v2";

    @Value("${milvus.host:localhost}")
    private String host;

    @Value("${milvus.port:19530}")
    private int port;

    private EmbeddingStore<TextSegment> embeddingStore;
    private HuggingFaceTokenizer tokenizer;
    private ZooModel<QAInput, float[]> model;

    public MilvusServiceImpl() {
        // 初始化Milvus客户端
        embeddingStore = MilvusEmbeddingStore.builder()
                .host(host)
                .port(port)
                .collectionName(COLLECTION_NAME)
                .dimension(384) // MiniLM-L6-v2的向量维度
                .build();
        
        // 初始化文本向量化模型
        initializeModel();
    }

    private void initializeModel() {
        try {
            // 初始化tokenizer
            tokenizer = HuggingFaceTokenizer.builder()
                    .optTokenizerName(MODEL_NAME)
                    .build();
            
            // 初始化模型
            Criteria<QAInput, float[]> criteria = Criteria.builder()
                    .setTypes(QAInput.class, float[].class)
                    .optModelUrls(MODEL_NAME)
                    .optProgress(new ProgressBar())
                    .build();
            
            model = criteria.loadModel();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize model", e);
        }
    }

    @Override
    public void insert(List<TextVector> documents) {
        try {
            List<TextSegment> segments = documents.stream()
                    .map(doc -> {
                        Metadata metadata = new Metadata();
                        metadata.add("id", doc.getId());
                        return TextSegment.from(doc.getText(), metadata);
                    })
                    .collect(Collectors.toList());
            
            List<Embedding> embeddings = documents.stream()
                    .map(doc -> {
                        float[] vector = new float[doc.getVector().size()];
                        for (int i = 0; i < doc.getVector().size(); i++) {
                            vector[i] = doc.getVector().get(i);
                        }
                        return new Embedding(vector);
                    })
                    .collect(Collectors.toList());
            
            embeddingStore.addAll(embeddings, segments);
        } catch (Exception e) {
            throw new RuntimeException("Failed to insert documents", e);
        }
    }

    @Override
    public List<TextVector> search(String query, int topK) {
        try {
            // 将查询文本转换为向量
            List<Float> queryVector = vectorizeText(query);
            float[] vectorArray = new float[queryVector.size()];
            for (int i = 0; i < queryVector.size(); i++) {
                vectorArray[i] = queryVector.get(i);
            }
            Embedding queryEmbedding = new Embedding(vectorArray);
            
            // 执行搜索
            List<EmbeddingMatch<TextSegment>> matches = embeddingStore.findRelevant(queryEmbedding, topK);
            
            // 转换结果
            return matches.stream()
                    .map(match -> {
                        TextVector vector = new TextVector();
                        vector.setId(match.embedded().metadata().get("id"));
                        vector.setText(match.embedded().text());
                        List<Float> vectorList = new ArrayList<>();
                        for (float value : match.embedding().vector()) {
                            vectorList.add(value);
                        }
                        vector.setVector(vectorList);
                        return vector;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to search documents", e);
        }
    }

    @Override
    public void delete(String documentId) {
        try {
            // TODO: 实现删除逻辑
            throw new UnsupportedOperationException("Delete operation not implemented yet");
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete document", e);
        }
    }

    @Override
    public void update(String documentId, TextVector document) {
        try {
            // 先删除旧文档
            delete(documentId);
            // 插入新文档
            insert(java.util.Collections.singletonList(document));
        } catch (Exception e) {
            throw new RuntimeException("Failed to update document", e);
        }
    }

    @Override
    public String vectorize(String text) {
        try {
            List<Float> vector = vectorizeText(text);
            return vector.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to vectorize text", e);
        }
    }

    private List<Float> vectorizeText(String text) {
        try {
            // 使用模型生成向量
            QAInput input = new QAInput(text, "");
            float[] embedding = model.newPredictor().predict(input);
            
            // 转换为List<Float>
            List<Float> vector = new ArrayList<>();
            for (float value : embedding) {
                vector.add(value);
            }
            
            return vector;
        } catch (TranslateException e) {
            throw new RuntimeException("Failed to vectorize text", e);
        }
    }
} 