package cn.stylefeng.guns.modular.knowledge.service.impl;

import cn.stylefeng.guns.modular.knowledge.entity.KnowledgeDocument;
import cn.stylefeng.guns.modular.knowledge.service.MilvusService;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.grpc.SearchResults;
import io.milvus.param.R;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.collection.ReleaseCollectionParam;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Milvus向量服务实现类
 */
@Service
public class MilvusServiceImpl implements MilvusService {

    private static final String COLLECTION_NAME = "knowledge_vectors";
    private static final String VECTOR_FIELD = "vector";
    private static final String TEXT_FIELD = "text";
    private static final String ID_FIELD = "id";
    private static final int VECTOR_DIM = 384; // MiniLM-L6-v2的向量维度

    @Autowired
    private MilvusServiceClient milvusClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void insertDocument(KnowledgeDocument document) {
        // 加载集合
        milvusClient.loadCollection(LoadCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build());

        // 准备插入数据
        List<List<Float>> vectors = new ArrayList<>();
        List<String> texts = new ArrayList<>();
        List<String> ids = new ArrayList<>();

        // 将文档内容向量化
        float[] vector = vectorizeContent(document.getContent());
        vectors.add(Arrays.asList(toObject(vector)));
        texts.add(document.getContent());
        ids.add(document.getId().toString());

        // 构建插入参数
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field(VECTOR_FIELD, vectors));
        fields.add(new InsertParam.Field(TEXT_FIELD, texts));
        fields.add(new InsertParam.Field(ID_FIELD, ids));

        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withFields(fields)
                .build();

        // 执行插入
        milvusClient.insert(insertParam);

        // 释放集合
        milvusClient.releaseCollection(ReleaseCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build());
    }

    @Override
    public List<String> searchSimilar(String query, int topK) {
        String cacheKey = "search:" + query + ":" + topK;
        
        // 尝试从缓存获取
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return (List<String>) cached;
        }

        // 加载集合
        milvusClient.loadCollection(LoadCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build());

        // 准备搜索参数
        float[] queryVector = vectorizeContent(query);
        List<List<Float>> searchVectors = new ArrayList<>();
        searchVectors.add(Arrays.asList(toObject(queryVector)));

        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withVectorFieldName(VECTOR_FIELD)
                .withVectors(searchVectors)
                .withTopK(topK)
                .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                .withOutFields(Arrays.asList(TEXT_FIELD))
                .build();

        // 执行搜索
        R<SearchResults> searchResults = milvusClient.search(searchParam);

        // 处理搜索结果
        List<String> results = new ArrayList<>();
        if (searchResults.getStatus() == R.Status.Success.getCode()) {
            SearchResults resultsData = searchResults.getData();
            for (int i = 0; i < resultsData.getResults().getFieldsDataCount(); i++) {
                results.add(resultsData.getResults().getFieldsData(i).getScalars().getStringData().getData(0));
            }
        }

        // 释放集合
        milvusClient.releaseCollection(ReleaseCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build());

        // 缓存结果
        redisTemplate.opsForValue().set(cacheKey, results, 1, TimeUnit.HOURS);

        return results;
    }

    @Override
    public void deleteDocument(String documentId) {
        // 加载集合
        milvusClient.loadCollection(LoadCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build());

        // 构建删除参数
        DeleteParam deleteParam = DeleteParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withExpr(String.format("%s == '%s'", ID_FIELD, documentId))
                .build();

        // 执行删除
        milvusClient.delete(deleteParam);

        // 释放集合
        milvusClient.releaseCollection(ReleaseCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build());
    }

    @Override
    public void updateDocument(String documentId, KnowledgeDocument document) {
        // 删除旧文档
        deleteDocument(documentId);
        // 插入新文档
        insertDocument(document);
    }

    @Override
    public String vectorize(String text) {
        float[] vector = vectorizeContent(text);
        return Arrays.toString(vector);
    }

    private float[] vectorizeContent(String content) {
        // TODO: 实现文本向量化逻辑，这里返回一个示例向量
        float[] vector = new float[VECTOR_DIM];
        for (int i = 0; i < VECTOR_DIM; i++) {
            vector[i] = (float) Math.random();
        }
        return vector;
    }

    private Float[] toObject(float[] array) {
        Float[] result = new Float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }
}