package cn.stylefeng.guns.modular.kernel.service.impl;

import cn.stylefeng.guns.modular.kernel.service.VectorService;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.grpc.SearchResults;
import io.milvus.param.R;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VectorServiceImpl implements VectorService {

    @Autowired
    private MilvusServiceClient milvusClient;

    private static final int VECTOR_DIM = 768; // BERT向量维度

    @Override
    public float[] vectorize(String text) {
        // 使用HanLP进行文本向量化
        List<Term> terms = HanLP.segment(text);
        float[] vector = new float[VECTOR_DIM];
        // 这里使用简单的词频统计作为示例
        // 实际应用中应该使用预训练模型生成向量
        for (Term term : terms) {
            int hash = term.word.hashCode() % VECTOR_DIM;
            vector[Math.abs(hash)] += 1.0f;
        }
        return vector;
    }

    @Override
    public List<float[]> vectorizeBatch(List<String> texts) {
        return texts.stream()
                .map(this::vectorize)
                .collect(Collectors.toList());
    }

    @Override
    public float calculateSimilarity(float[] vector1, float[] vector2) {
        // 将float[]转换为double[]
        double[] v1 = new double[vector1.length];
        double[] v2 = new double[vector2.length];
        for (int i = 0; i < vector1.length; i++) {
            v1[i] = vector1[i];
            v2[i] = vector2[i];
        }
        RealVector rv1 = new ArrayRealVector(v1);
        RealVector rv2 = new ArrayRealVector(v2);
        return (float) rv1.cosine(rv2);
    }

    @Override
    public void saveVector(String collectionName, float[] vector, String id) {
        // 创建集合（如果不存在）
        createCollectionIfNotExists(collectionName);

        // 插入向量
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field("id", Arrays.asList(id)));
        fields.add(new InsertParam.Field("vector", Arrays.asList(vector)));

        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(collectionName)
                .withFields(fields)
                .build();

        milvusClient.insert(insertParam);
    }

    @Override
    public List<String> searchSimilar(String collectionName, float[] vector, int topK) {
        List<String> results = new ArrayList<>();
        
        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(collectionName)
                .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                .withMetricType(io.milvus.param.dml.MetricType.COSINE)
                .withOutFields(Arrays.asList("id"))
                .withTopK(topK)
                .withVectors(Arrays.asList(vector))
                .withVectorFieldName("vector")
                .build();

        R<SearchResults> searchResponse = milvusClient.search(searchParam);
        if (searchResponse.getStatus() == R.Status.Success.getCode()) {
            SearchResults searchResults = searchResponse.getData();
            // 处理搜索结果
            searchResults.getResults().getFieldsDataList().forEach(field -> {
                results.add(field.getScalars().getStringData().getData(0));
            });
        }

        return results;
    }

    private void createCollectionIfNotExists(String collectionName) {
        List<FieldType> fieldTypes = new ArrayList<>();
        fieldTypes.add(FieldType.newBuilder()
                .withName("id")
                .withDataType(DataType.VarChar)
                .withMaxLength(100)
                .withPrimaryKey(true)
                .withAutoID(false)
                .build());
        fieldTypes.add(FieldType.newBuilder()
                .withName("vector")
                .withDataType(DataType.FloatVector)
                .withDimension(VECTOR_DIM)
                .build());

        CreateCollectionParam createCollectionParam = CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withFieldTypes(fieldTypes)
                .build();

        milvusClient.createCollection(createCollectionParam);
    }
} 