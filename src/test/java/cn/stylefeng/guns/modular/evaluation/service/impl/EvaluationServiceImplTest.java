package cn.stylefeng.guns.modular.evaluation.service.impl;

import cn.stylefeng.guns.modular.evaluation.entity.EvaluationResult;
import cn.stylefeng.guns.modular.text.service.TextPreprocessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class EvaluationServiceImplTest {

    @Mock
    private TextPreprocessingService textPreprocessingService;

    @InjectMocks
    private EvaluationServiceImpl evaluationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(textPreprocessingService.preprocessText(anyString())).thenAnswer(i -> i.getArguments()[0]);
        when(textPreprocessingService.splitIntoSentences(anyString())).thenAnswer(i -> 
            Arrays.asList(((String) i.getArguments()[0]).split("[。！？]")));
        when(textPreprocessingService.splitIntoParagraphs(anyString())).thenAnswer(i -> 
            Arrays.asList(((String) i.getArguments()[0]).split("\\n\\s*\\n")));
    }

    @Test
    void testEvaluateResponse_WithPerfectAnswer() {
        // 准备测试数据
        String question = "什么是人工智能？";
        String answer = "人工智能（AI）是计算机科学的一个分支，它致力于创造能够模拟人类智能的机器。首先，AI系统可以学习、推理和解决问题。其次，AI可以处理自然语言，理解人类语言。例如，语音助手和聊天机器人就是AI的典型应用。最后，AI在医疗、金融等领域都有广泛应用。";
        List<String> context = Arrays.asList("AI技术发展迅速", "机器学习是AI的重要分支");

        // 执行测试
        EvaluationResult result = evaluationService.evaluateResponse(question, answer, context);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.getRelevanceScore() > 0.8);
        assertTrue(result.getCompletenessScore() > 0.8);
        assertTrue(result.getCoherenceScore() > 0.8);
        assertTrue(result.getOverallScore() > 0.8);
        assertTrue(result.getSuggestions().isEmpty());
    }

    @Test
    void testEvaluateResponse_WithPoorAnswer() {
        // 准备测试数据
        String question = "什么是人工智能？";
        String answer = "AI就是机器。";
        List<String> context = Arrays.asList("AI技术发展迅速", "机器学习是AI的重要分支");

        // 执行测试
        EvaluationResult result = evaluationService.evaluateResponse(question, answer, context);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.getRelevanceScore() < 0.5);
        assertTrue(result.getCompletenessScore() < 0.5);
        assertTrue(result.getCoherenceScore() < 0.5);
        assertTrue(result.getOverallScore() < 0.5);
        assertFalse(result.getSuggestions().isEmpty());
    }

    @Test
    void testEvaluateResponse_WithEmptyContext() {
        // 准备测试数据
        String question = "什么是人工智能？";
        String answer = "人工智能（AI）是计算机科学的一个分支，它致力于创造能够模拟人类智能的机器。";
        List<String> context = null;

        // 执行测试
        EvaluationResult result = evaluationService.evaluateResponse(question, answer, context);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.getRelevanceScore() > 0.6);
        assertTrue(result.getCompletenessScore() > 0.6);
        assertTrue(result.getCoherenceScore() > 0.6);
        assertTrue(result.getOverallScore() > 0.6);
    }

    @Test
    void testCalculateRelevance() {
        // 准备测试数据
        String question = "什么是人工智能？";
        String answer = "人工智能（AI）是计算机科学的一个分支，它致力于创造能够模拟人类智能的机器。";
        List<String> context = Arrays.asList("AI技术发展迅速", "机器学习是AI的重要分支");

        // 执行测试
        double score = evaluationService.calculateRelevance(question, answer, context);

        // 验证结果
        assertTrue(score > 0.6);
    }

    @Test
    void testCalculateCompleteness() {
        // 准备测试数据
        String question = "什么是人工智能？";
        String answer = "人工智能（AI）是计算机科学的一个分支，它致力于创造能够模拟人类智能的机器。首先，AI系统可以学习、推理和解决问题。其次，AI可以处理自然语言，理解人类语言。例如，语音助手和聊天机器人就是AI的典型应用。";

        // 执行测试
        double score = evaluationService.calculateCompleteness(question, answer);

        // 验证结果
        assertTrue(score > 0.7);
    }

    @Test
    void testCalculateCoherence() {
        // 准备测试数据
        String answer = "人工智能（AI）是计算机科学的一个分支，它致力于创造能够模拟人类智能的机器。首先，AI系统可以学习、推理和解决问题。其次，AI可以处理自然语言，理解人类语言。例如，语音助手和聊天机器人就是AI的典型应用。";

        // 执行测试
        double score = evaluationService.calculateCoherence(answer);

        // 验证结果
        assertTrue(score > 0.7);
    }
} 