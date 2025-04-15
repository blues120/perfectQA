package cn.stylefeng.guns.modular.common.service.impl;

import cn.stylefeng.guns.modular.common.service.SmartChatService;
import cn.stylefeng.guns.modular.text.service.TextPreprocessingService;
import cn.stylefeng.guns.modular.evaluation.service.EvaluationService;
import cn.stylefeng.guns.modular.evaluation.entity.EvaluationResult;
import cn.stylefeng.guns.modular.chat.service.ChatContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SmartChatServiceImpl implements SmartChatService {

    @Autowired
    private TextPreprocessingService textPreprocessingService;

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private ChatContextService chatContextService;

    // 存储用户会话上下文
    private final Map<String, List<Map<String, Object>>> userContexts = new ConcurrentHashMap<>();
    
    // 存储可用模型
    private final Map<String, String> availableModels = new HashMap<String, String>() {{
        put("gpt-3.5", "GPT-3.5 Turbo");
        put("gpt-4", "GPT-4");
        put("claude", "Claude");
    }};

    @Override
    public String chat(String userId, String sessionId, String question, String district, String department, String modelName) {
        try {
            // 1. 预处理问题
            String processedQuestion = textPreprocessingService.preprocessText(question);

            // 2. 获取上下文
            Map<String, Object> contextMap = getChatContext(userId);
            List<Map<String, Object>> context = (List<Map<String, Object>>) contextMap.get("context");

            // 3. 生成回答
            String answer = generateAnswer(processedQuestion, context, modelName);

            // 4. 评估回答
            List<String> contextTexts = extractContextTexts(context);
            EvaluationResult evaluation = evaluationService.evaluateResponse(processedQuestion, answer, contextTexts);

            // 5. 保存对话历史
            saveChatHistory(userId, sessionId, question, answer, evaluation);

            // 6. 如果评估分数低，尝试改进回答
            if (evaluation.getOverallScore() < 0.7) {
                answer = improveAnswer(answer, evaluation.getSuggestions(), context, modelName);
            }

            return answer;
        } catch (Exception e) {
            throw new RuntimeException("聊天处理失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> getChatContext(String userId) {
        List<Map<String, Object>> context = userContexts.getOrDefault(userId, new ArrayList<>());
        Map<String, Object> result = new HashMap<>();
        result.put("context", context);
        return result;
    }

    @Override
    public Boolean clearChatContext(String userId) {
        userContexts.remove(userId);
        return true;
    }

    @Override
    public Map<String, Object> analyzeChat(String message) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("length", message.length());
        analysis.put("wordCount", message.split("\\s+").length);
        analysis.put("sentiment", analyzeSentiment(message));
        return analysis;
    }

    @Override
    public List<String> getSuggestedQuestions(String userId, String district, String department) {
        List<String> suggestions = new ArrayList<>();
        
        // 添加基于上下文的建议
        Map<String, Object> contextMap = getChatContext(userId);
        List<Map<String, Object>> context = (List<Map<String, Object>>) contextMap.get("context");
        if (!context.isEmpty()) {
            suggestions.addAll(generateContextBasedSuggestions(context));
        }
        
        // 添加基于地区和部门的建议
        if (district != null && department != null) {
            suggestions.addAll(generateDomainBasedSuggestions(district, department));
        }
        
        return suggestions;
    }

    @Override
    public Map<String, String> getAvailableModels() {
        return availableModels;
    }

    private String generateAnswer(String question, List<Map<String, Object>> context, String modelName) {
        // 这里应该调用实际的模型服务
        // 目前返回一个简单的示例回答
        return "这是一个示例回答。实际实现中应该调用模型服务来生成回答。";
    }

    private String improveAnswer(String originalAnswer, List<String> suggestions, List<Map<String, Object>> context, String modelName) {
        // 这里应该实现回答改进逻辑
        // 目前返回原始回答
        return originalAnswer;
    }

    private void saveChatHistory(String userId, String sessionId, String question, String answer, EvaluationResult evaluation) {
        Map<String, Object> chatEntry = new HashMap<>();
        chatEntry.put("question", question);
        chatEntry.put("answer", answer);
        chatEntry.put("evaluation", convertEvaluationToMap(evaluation));
        chatEntry.put("timestamp", System.currentTimeMillis());

        List<Map<String, Object>> userContext = userContexts.computeIfAbsent(userId, k -> new ArrayList<>());
        userContext.add(chatEntry);
    }

    private String analyzeSentiment(String message) {
        // 简单的情绪分析实现
        if (message.contains("谢谢") || message.contains("感谢")) {
            return "positive";
        } else if (message.contains("不好") || message.contains("不行")) {
            return "negative";
        }
        return "neutral";
    }

    private List<String> generateContextBasedSuggestions(List<Map<String, Object>> context) {
        List<String> suggestions = new ArrayList<>();
        // 基于上下文生成建议问题
        suggestions.add("您能详细解释一下吗？");
        suggestions.add("还有其他相关问题吗？");
        return suggestions;
    }

    private List<String> generateDomainBasedSuggestions(String district, String department) {
        List<String> suggestions = new ArrayList<>();
        // 基于地区和部门生成建议问题
        suggestions.add("关于" + district + "的" + department + "有什么最新政策？");
        suggestions.add("如何申请" + district + "的" + department + "相关服务？");
        return suggestions;
    }

    private List<String> extractContextTexts(List<Map<String, Object>> context) {
        List<String> texts = new ArrayList<>();
        for (Map<String, Object> entry : context) {
            texts.add((String) entry.get("question"));
            texts.add((String) entry.get("answer"));
        }
        return texts;
    }

    private Map<String, Object> convertEvaluationToMap(EvaluationResult evaluation) {
        Map<String, Object> map = new HashMap<>();
        map.put("relevanceScore", evaluation.getRelevanceScore());
        map.put("completenessScore", evaluation.getCompletenessScore());
        map.put("coherenceScore", evaluation.getCoherenceScore());
        map.put("overallScore", evaluation.getOverallScore());
        map.put("suggestions", evaluation.getSuggestions());
        return map;
    }
} 