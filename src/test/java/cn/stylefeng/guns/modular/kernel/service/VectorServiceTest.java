package cn.stylefeng.guns.modular.kernel.service;

import cn.stylefeng.guns.modular.kernel.service.impl.VectorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class VectorServiceTest {

    @Autowired
    private VectorService vectorService;

    private static final String TEST_COLLECTION = "test_vectors";

    @BeforeEach
    void setUp() {
        // 清理测试数据
    }

    @Test
    void testVectorize() {
        String text = "这是一个测试文本";
        float[] vector = vectorService.vectorize(text);
        assertNotNull(vector);
        assertTrue(vector.length > 0);
    }

    @Test
    void testVectorizeBatch() {
        List<String> texts = Arrays.asList(
            "这是第一个测试文本",
            "这是第二个测试文本"
        );
        List<float[]> vectors = vectorService.vectorizeBatch(texts);
        assertNotNull(vectors);
        assertEquals(2, vectors.size());
    }

    @Test
    void testCalculateSimilarity() {
        float[] vector1 = {1.0f, 2.0f, 3.0f};
        float[] vector2 = {2.0f, 4.0f, 6.0f};
        float similarity = vectorService.calculateSimilarity(vector1, vector2);
        assertTrue(similarity >= -1.0f && similarity <= 1.0f);
    }

    @Test
    void testSaveAndSearchVector() {
        // 保存向量
        float[] vector = {1.0f, 2.0f, 3.0f};
        String id = "test_id";
        vectorService.saveVector(TEST_COLLECTION, vector, id);

        // 搜索相似向量
        List<String> results = vectorService.searchSimilar(TEST_COLLECTION, vector, 1);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(id, results.get(0));
    }
} 