package cn.stylefeng.guns.modular.chat.service.impl;

import cn.stylefeng.guns.modular.chat.service.LLMService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class LLMServiceImpl implements LLMService {
    
    @Value("${llm.api.url}")
    private String llmApiUrl;
    
    @Value("${llm.api.key}")
    private String llmApiKey;
    
    @Value("${llm.api.model:gpt-3.5-turbo}")
    private String defaultModel;
    
    @Value("${llm.api.max_tokens:2000}")
    private int maxTokens;
    
    @Value("${llm.api.temperature:0.7}")
    private double temperature;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    
    @Override
    public String processMessage(String message) {
        return generateResponse(message);
    }
    
    @Override
    public String processMessageWithContext(String message, String context) {
        String prompt = String.format("上下文: %s\n问题: %s", context, message);
        return generateResponse(prompt);
    }
    
    @Override
    public String processConversation(List<String> conversation, String currentMessage) {
        StringBuilder promptBuilder = new StringBuilder();
        
        for (int i = 0; i < conversation.size(); i++) {
            promptBuilder.append("用户: ").append(conversation.get(i)).append("\n");
            if (i < conversation.size() - 1) {
                promptBuilder.append("助手: ").append(conversation.get(i + 1)).append("\n");
                i++;
            }
        }
        
        promptBuilder.append("用户: ").append(currentMessage);
        
        return generateResponse(promptBuilder.toString());
    }
    
    @Override
    public String generateResponse(String prompt) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("model", defaultModel);
            request.put("messages", createMessages(prompt));
            request.put("max_tokens", maxTokens);
            request.put("temperature", temperature);
            
            Map<String, Object> response = restTemplate.postForObject(
                llmApiUrl,
                request,
                Map.class
            );
            
            return extractResponseText(response);
        } catch (Exception e) {
            throw new RuntimeException("生成响应失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CompletableFuture<String> generateResponseAsync(String prompt) {
        return CompletableFuture.supplyAsync(() -> generateResponse(prompt), executorService);
    }
    
    @Override
    public List<String> generateMultipleResponses(String prompt, int count) {
        try {
            List<String> responses = new ArrayList<>();
            List<CompletableFuture<String>> futures = new ArrayList<>();
            
            for (int i = 0; i < count; i++) {
                futures.add(generateResponseAsync(prompt));
            }
            
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .join();
                
            for (CompletableFuture<String> future : futures) {
                responses.add(future.get());
            }
            
            return responses;
        } catch (Exception e) {
            throw new RuntimeException("生成多个响应失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, Object> analyzeText(String text, String analysisType) {
        try {
            String prompt = String.format("请分析以下文本的%s: %s", analysisType, text);
            String response = generateResponse(prompt);
            
            Map<String, Object> analysis = new HashMap<>();
            analysis.put("text", text);
            analysis.put("analysis_type", analysisType);
            analysis.put("result", response);
            
            // 尝试解析结果为JSON
            try {
                Map<String, Object> jsonResult = objectMapper.readValue(response, Map.class);
                analysis.put("parsed_result", jsonResult);
            } catch (Exception e) {
                // 如果解析失败，保持原始结果
            }
            
            return analysis;
        } catch (Exception e) {
            throw new RuntimeException("文本分析失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String summarizeText(String text, int maxLength) {
        try {
            String prompt = String.format("请将以下文本总结为不超过%d字的摘要: %s", maxLength, text);
            return generateResponse(prompt);
        } catch (Exception e) {
            throw new RuntimeException("文本总结失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String translateText(String text, String targetLanguage) {
        try {
            String prompt = String.format("请将以下文本翻译成%s: %s", targetLanguage, text);
            return generateResponse(prompt);
        } catch (Exception e) {
            throw new RuntimeException("文本翻译失败: " + e.getMessage(), e);
        }
    }
    
    private List<Map<String, String>> createMessages(String prompt) {
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        return messages;
    }
    
    private String extractResponseText(Map<String, Object> response) {
        if (response == null || !response.containsKey("choices")) {
            return "无法生成响应";
        }
        
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices.isEmpty()) {
            return "无法生成响应";
        }
        
        Map<String, Object> choice = choices.get(0);
        if (!choice.containsKey("message")) {
            return "无法生成响应";
        }
        
        Map<String, Object> message = (Map<String, Object>) choice.get("message");
        if (!message.containsKey("content")) {
            return "无法生成响应";
        }
        
        return (String) message.get("content");
    }
} 