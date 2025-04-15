package cn.stylefeng.guns.modular.evaluation.service.impl;

import cn.stylefeng.guns.modular.evaluation.entity.EvaluationResult;
import cn.stylefeng.guns.modular.evaluation.service.EvaluationService;
import cn.stylefeng.guns.modular.text.service.TextPreprocessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Arrays;

@Service
public class EvaluationServiceImpl implements EvaluationService {
    
    private static final double RELEVANCE_THRESHOLD = 0.7;
    private static final double COMPLETENESS_THRESHOLD = 0.6;
    private static final double COHERENCE_THRESHOLD = 0.5;
    
    @Autowired
    private TextPreprocessingService textPreprocessingService;
    
    private static final String[] CHINESE_TRANSITION_WORDS = {
        "首先", "其次", "然后", "最后", "因此", "所以", "因为", "但是",
        "此外", "另外", "同时", "然而", "不过", "而且", "并且", "或者"
    };
    
    private static final String[] CHINESE_EXAMPLE_MARKERS = {
        "例如", "比如", "举例", "比如说", "譬如", "比方说", "具体来说"
    };
    
    @Override
    @Cacheable(value = "evaluation", key = "#question + '|' + #answer + '|' + #context.hashCode()")
    public EvaluationResult evaluateResponse(String question, String answer, List<String> context) {
        EvaluationResult result = new EvaluationResult();
        
        // 预处理文本
        String processedQuestion = textPreprocessingService.preprocessText(question);
        String processedAnswer = textPreprocessingService.preprocessText(answer);
        List<String> processedContext = new ArrayList<>();
        if (context != null) {
            for (String ctx : context) {
                processedContext.add(textPreprocessingService.preprocessText(ctx));
            }
        }
        
        // 计算相关性分数
        double relevanceScore = calculateRelevance(processedQuestion, processedAnswer, processedContext);
        result.setRelevanceScore(relevanceScore);
        
        // 计算完整性分数
        double completenessScore = calculateCompleteness(processedQuestion, processedAnswer);
        result.setCompletenessScore(completenessScore);
        
        // 计算连贯性分数
        double coherenceScore = calculateCoherence(processedAnswer);
        result.setCoherenceScore(coherenceScore);
        
        // 计算总体分数
        double overallScore = (relevanceScore + completenessScore + coherenceScore) / 3;
        result.setOverallScore(overallScore);
        
        // 生成建议
        List<String> suggestions = generateSuggestions(relevanceScore, completenessScore, coherenceScore);
        result.setSuggestions(suggestions);
        
        return result;
    }
    
    // 修改为包级访问权限，允许测试类访问
    @Cacheable(value = "relevance", key = "#question + '|' + #answer + '|' + #context.hashCode()")
    double calculateRelevance(String question, String answer, List<String> context) {
        // 计算问题关键词与回答的匹配度
        double questionMatchScore = calculateKeywordMatch(question, answer);
        
        // 计算上下文与回答的匹配度
        double contextMatchScore = 0.0;
        if (context != null && !context.isEmpty()) {
            for (String ctx : context) {
                contextMatchScore += calculateKeywordMatch(ctx, answer);
            }
            contextMatchScore /= context.size();
        }
        
        // 综合计算相关性分数
        return (questionMatchScore * 0.7 + contextMatchScore * 0.3);
    }
    
    // 修改为包级访问权限，允许测试类访问
    @Cacheable(value = "completeness", key = "#question + '|' + #answer")
    double calculateCompleteness(String question, String answer) {
        // 检查是否包含关键信息
        boolean hasKeyInfo = checkKeyInformation(question, answer);
        
        // 检查回答长度
        double lengthScore = calculateLengthScore(answer);
        
        // 检查是否包含具体示例
        boolean hasExamples = checkForExamples(answer);
        
        // 检查是否包含具体数据
        boolean hasData = checkForData(answer);
        
        // 综合计算完整性分数
        double score = 0.0;
        if (hasKeyInfo) score += 0.3;
        score += lengthScore * 0.3;
        if (hasExamples) score += 0.2;
        if (hasData) score += 0.2;
        
        return score;
    }
    
    // 修改为包级访问权限，允许测试类访问
    @Cacheable(value = "coherence", key = "#answer")
    double calculateCoherence(String answer) {
        // 检查句子结构
        double structureScore = checkSentenceStructure(answer);
        
        // 检查逻辑连贯性
        double logicScore = checkLogicalFlow(answer);
        
        // 检查语法正确性
        double grammarScore = checkGrammar(answer);
        
        // 检查段落结构
        double paragraphScore = checkParagraphStructure(answer);
        
        // 综合计算连贯性分数
        return (structureScore * 0.3 + logicScore * 0.3 + grammarScore * 0.2 + paragraphScore * 0.2);
    }
    
    private List<String> generateSuggestions(double relevanceScore, double completenessScore, double coherenceScore) {
        List<String> suggestions = new ArrayList<>();
        
        if (relevanceScore < RELEVANCE_THRESHOLD) {
            suggestions.add("建议提高回答与问题的相关性，确保回答直接针对问题要点。");
        }
        
        if (completenessScore < COMPLETENESS_THRESHOLD) {
            suggestions.add("建议提供更完整的回答，包含更多关键信息、具体示例和数据支持。");
        }
        
        if (coherenceScore < COHERENCE_THRESHOLD) {
            suggestions.add("建议提高回答的连贯性，确保逻辑清晰，表达流畅，段落结构合理。");
        }
        
        return suggestions;
    }
    
    private double calculateKeywordMatch(String text1, String text2) {
        // 使用预处理服务分割文本
        List<String> words1 = Arrays.asList(text1.split("\\s+"));
        List<String> words2 = Arrays.asList(text2.split("\\s+"));
        
        int matches = 0;
        for (String word : words1) {
            if (words2.contains(word)) {
                matches++;
            }
        }
        return (double) matches / words1.size();
    }
    
    private boolean checkKeyInformation(String question, String answer) {
        List<String> keyWords = Arrays.asList(question.split("\\s+"));
        List<String> answerWords = Arrays.asList(answer.split("\\s+"));
        
        for (String word : keyWords) {
            if (!answerWords.contains(word)) {
                return false;
            }
        }
        return true;
    }
    
    private double calculateLengthScore(String answer) {
        int length = answer.length();
        if (length < 100) return 0.3;
        if (length < 200) return 0.6;
        if (length < 400) return 0.8;
        return 1.0;
    }
    
    private boolean checkForExamples(String answer) {
        for (String marker : CHINESE_EXAMPLE_MARKERS) {
            if (answer.contains(marker)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean checkForData(String answer) {
        // 检查是否包含数字、百分比等数据
        return answer.matches(".*\\d+.*") || 
               answer.contains("%") || 
               answer.contains("数据") || 
               answer.contains("统计");
    }
    
    private double checkSentenceStructure(String answer) {
        List<String> sentences = textPreprocessingService.splitIntoSentences(answer);
        int wellStructured = 0;
        
        for (String sentence : sentences) {
            if (sentence.trim().matches("^[^。！？]+[。！？]$")) {
                wellStructured++;
            }
        }
        return (double) wellStructured / sentences.size();
    }
    
    private double checkLogicalFlow(String answer) {
        int transitions = 0;
        for (String word : CHINESE_TRANSITION_WORDS) {
            if (answer.contains(word)) {
                transitions++;
            }
        }
        return Math.min(1.0, transitions / 4.0);
    }
    
    private double checkGrammar(String answer) {
        List<String> sentences = textPreprocessingService.splitIntoSentences(answer);
        int correctSentences = 0;
        
        for (String sentence : sentences) {
            if (sentence.trim().matches("^[^。！？]+[。！？]$")) {
                correctSentences++;
            }
        }
        return (double) correctSentences / sentences.size();
    }
    
    private double checkParagraphStructure(String answer) {
        List<String> paragraphs = textPreprocessingService.splitIntoParagraphs(answer);
        if (paragraphs.size() < 2) {
            return 0.5;
        }
        
        int wellStructured = 0;
        for (String paragraph : paragraphs) {
            if (paragraph.trim().length() > 50) {
                wellStructured++;
            }
        }
        return (double) wellStructured / paragraphs.size();
    }
} 