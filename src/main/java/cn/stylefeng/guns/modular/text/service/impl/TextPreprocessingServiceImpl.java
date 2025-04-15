package cn.stylefeng.guns.modular.text.service.impl;

import cn.stylefeng.guns.modular.text.service.TextPreprocessingService;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class TextPreprocessingServiceImpl implements TextPreprocessingService {

    private static final Pattern SENTENCE_PATTERN = Pattern.compile("[^.!?]+[.!?]+");
    private static final Pattern PARAGRAPH_PATTERN = Pattern.compile("\\n\\s*\\n");

    @Override
    public String preprocessText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        // 移除HTML标签
        text = text.replaceAll("<[^>]+>", "");
        
        // 移除特殊字符
        text = text.replaceAll("[^\\p{L}\\p{N}\\p{P}\\p{Z}]", "");
        
        // 标准化空白字符
        text = text.replaceAll("\\s+", " ").trim();
        
        return text;
    }

    @Override
    public List<String> splitIntoSentences(String text) {
        List<String> sentences = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return sentences;
        }
        
        text = preprocessText(text);
        java.util.regex.Matcher matcher = SENTENCE_PATTERN.matcher(text);
        while (matcher.find()) {
            sentences.add(matcher.group().trim());
        }
        
        return sentences;
    }

    @Override
    public List<String> splitIntoParagraphs(String text) {
        List<String> paragraphs = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return paragraphs;
        }
        
        text = preprocessText(text);
        String[] parts = PARAGRAPH_PATTERN.split(text);
        for (String part : parts) {
            if (!part.trim().isEmpty()) {
                paragraphs.add(part.trim());
            }
        }
        
        return paragraphs;
    }

    @Override
    public List<String> createTextChunks(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        
        // 按句子分割
        String[] sentences = text.split("[.!?]+");
        
        StringBuilder currentChunk = new StringBuilder();
        int currentSize = 0;
        
        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (sentence.isEmpty()) continue;
            
            if (currentSize + sentence.length() <= chunkSize) {
                if (currentChunk.length() > 0) {
                    currentChunk.append(" ");
                }
                currentChunk.append(sentence);
                currentSize += sentence.length();
            } else {
                if (currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString());
                }
                currentChunk = new StringBuilder(sentence);
                currentSize = sentence.length();
            }
        }
        
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString());
        }
        
        return chunks;
    }

    @Override
    public Map<String, Object> extractMetadata(String text) {
        Map<String, Object> metadata = new HashMap<>();
        
        // 提取标题
        String title = extractTitle(text);
        metadata.put("title", title);
        
        // 提取作者
        String author = extractAuthor(text);
        metadata.put("author", author);
        
        // 提取日期
        Date date = extractDate(text);
        metadata.put("date", date);
        
        // 提取语言
        String language = detectLanguage(text);
        metadata.put("language", language);
        
        // 提取摘要
        String summary = generateSummary(text);
        metadata.put("summary", summary);
        
        // 提取标签
        List<String> tags = extractTags(text);
        metadata.put("tags", tags);
        
        return metadata;
    }
    
    // 私有方法：提取标题
    private String extractTitle(String text) {
        // 简单的标题提取实现
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.length() > 0 && line.length() < 100) {
                return line.trim();
            }
        }
        return "";
    }
    
    // 私有方法：提取作者
    private String extractAuthor(String text) {
        // 简单的作者提取实现
        if (text.contains("作者：") || text.contains("作者:")) {
            int start = text.indexOf("作者：") + 3;
            int end = text.indexOf("\n", start);
            if (end == -1) end = text.length();
            return text.substring(start, end).trim();
        }
        return "未知";
    }
    
    // 私有方法：提取日期
    private Date extractDate(String text) {
        // 简单的日期提取实现
        // 实际项目中应该使用更复杂的日期解析
        return new Date();
    }
    
    // 私有方法：检测语言
    private String detectLanguage(String text) {
        // 简单的语言检测实现
        // 实际项目中应该使用专业的语言检测工具
        return "zh";
    }
    
    // 私有方法：生成摘要
    private String generateSummary(String text) {
        // 简单的摘要生成实现
        // 实际项目中应该使用专业的摘要生成算法
        String[] sentences = text.split("[.!?]+");
        if (sentences.length > 0) {
            return sentences[0].trim();
        }
        return "";
    }
    
    // 私有方法：提取标签
    private List<String> extractTags(String text) {
        // 简单的标签提取实现
        List<String> tags = new ArrayList<>();
        
        // 提取关键词作为标签
        List<String> keywords = extractKeywords(tokenize(text));
        tags.addAll(keywords);
        
        // 提取实体作为标签
        List<String> entities = extractEntities(text);
        tags.addAll(entities);
        
        return tags;
    }
    
    // 私有方法：分词
    private List<String> tokenize(String text) {
        // 简单的分词实现，实际项目中应该使用专业的分词工具
        return Arrays.asList(text.split("\\s+"));
    }
    
    // 私有方法：提取关键词
    private List<String> extractKeywords(List<String> tokens) {
        Map<String, Integer> wordFreq = new HashMap<>();
        
        // 统计词频
        for (String token : tokens) {
            if (token.length() > 1) { // 忽略单字
                wordFreq.merge(token, 1, Integer::sum);
            }
        }
        
        // 按词频排序
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(wordFreq.entrySet());
        sortedEntries.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
        
        // 提取前10个关键词
        List<String> keywords = new ArrayList<>();
        for (int i = 0; i < Math.min(10, sortedEntries.size()); i++) {
            keywords.add(sortedEntries.get(i).getKey());
        }
        
        return keywords;
    }
    
    // 私有方法：提取实体
    private List<String> extractEntities(String text) {
        List<String> entities = new ArrayList<>();
        
        // 简单的实体提取实现，实际项目中应该使用专业的NER工具
        String[] words = text.split("\\s+");
        for (String word : words) {
            if (word.length() > 1 && Character.isUpperCase(word.charAt(0))) {
                entities.add(word);
            }
        }
        
        return entities;
    }
} 