package cn.stylefeng.guns.modular.chat.service.impl;

import cn.stylefeng.guns.modular.chat.service.WolframService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WolframServiceImpl implements WolframService {
    
    @Value("${wolfram.api.url:http://api.wolframalpha.com/v1/result}")
    private String wolframApiUrl;
    
    @Value("${wolfram.api.key}")
    private String wolframApiKey;
    
    @Value("${wolfram.api.timeout:30}")
    private int timeout;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public String calculate(String expression) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(wolframApiUrl)
                .queryParam("appid", wolframApiKey)
                .queryParam("i", expression)
                .queryParam("timeout", timeout)
                .build()
                .toUriString();
                
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            throw new RuntimeException("执行数学计算失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getSteps(String expression) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(wolframApiUrl)
                .queryParam("appid", wolframApiKey)
                .queryParam("i", expression)
                .queryParam("podtitle", "Step-by-step solution")
                .queryParam("timeout", timeout)
                .build()
                .toUriString();
                
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            throw new RuntimeException("获取计算步骤失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String convertUnit(double value, String fromUnit, String toUnit) {
        try {
            String expression = String.format("%f %s to %s", value, fromUnit, toUnit);
            return calculate(expression);
        } catch (Exception e) {
            throw new RuntimeException("执行单位转换失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getFormulaImage(String expression) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(wolframApiUrl)
                .queryParam("appid", wolframApiKey)
                .queryParam("i", expression)
                .queryParam("format", "image")
                .queryParam("timeout", timeout)
                .build()
                .toUriString();
                
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            throw new RuntimeException("获取公式图像失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, Object> analyzeData(String data, String analysisType) {
        try {
            String expression = String.format("analyze %s %s", data, analysisType);
            String result = calculate(expression);
            
            Map<String, Object> analysis = new HashMap<>();
            analysis.put("result", result);
            analysis.put("type", analysisType);
            analysis.put("data", data);
            
            // 尝试解析结果为JSON
            try {
                Map<String, Object> jsonResult = objectMapper.readValue(result, Map.class);
                analysis.put("parsed_result", jsonResult);
            } catch (Exception e) {
                // 如果解析失败，保持原始结果
            }
            
            return analysis;
        } catch (Exception e) {
            throw new RuntimeException("执行数据分析失败: " + e.getMessage(), e);
        }
    }
} 