package cn.stylefeng.guns.modular.common.service.impl;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TextPreprocessingService {
    private static final int MAX_CHUNK_SIZE = 1000;
    private static final int MIN_CHUNK_SIZE = 200;

    public String preprocessText(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }

        // 1. 清理特殊字符和格式
        content = content.replaceAll("[\\x00-\\x1F\\x7F]", "")
                        .replaceAll("\\s+", " ")
                        .trim();

        // 2. 标准化标点符号
        content = content.replaceAll("，", ",")
                        .replaceAll("。", ".")
                        .replaceAll("；", ";");

        // 3. 处理数字和单位
        content = content.replaceAll("(\\d+)([a-zA-Z]+)", "$1 $2");

        // 4. 处理缩写
        content = content.replaceAll("e\\.g\\.", "for example")
                        .replaceAll("i\\.e\\.", "that is");

        return content;
    }

    public List<String> splitIntoSentences(String text) {
        // 使用正则表达式分割句子
        Pattern pattern = Pattern.compile("(?<=[.!?])\\s+");
        return Arrays.asList(pattern.split(text));
    }

    public List<String> splitIntoParagraphs(String text) {
        // 按段落分割
        return Arrays.asList(text.split("\\n\\s*\\n"));
    }

    public List<String> createChunks(String text) {
        List<String> chunks = new ArrayList<>();
        List<String> paragraphs = splitIntoParagraphs(text);

        StringBuilder currentChunk = new StringBuilder();
        int currentLength = 0;

        for (String paragraph : paragraphs) {
            if (currentLength + paragraph.length() > MAX_CHUNK_SIZE) {
                if (currentLength > MIN_CHUNK_SIZE) {
                    chunks.add(currentChunk.toString().trim());
                    currentChunk = new StringBuilder();
                    currentLength = 0;
                }

                if (paragraph.length() > MAX_CHUNK_SIZE) {
                    // 处理过长的段落
                    List<String> sentences = splitIntoSentences(paragraph);
                    for (String sentence : sentences) {
                        if (currentLength + sentence.length() > MAX_CHUNK_SIZE) {
                            if (currentLength > MIN_CHUNK_SIZE) {
                                chunks.add(currentChunk.toString().trim());
                                currentChunk = new StringBuilder();
                                currentLength = 0;
                            }
                            if (sentence.length() > MAX_CHUNK_SIZE) {
                                // 处理过长的句子
                                chunks.addAll(splitLongText(sentence));
                            } else {
                                currentChunk.append(sentence).append(" ");
                                currentLength += sentence.length();
                            }
                        } else {
                            currentChunk.append(sentence).append(" ");
                            currentLength += sentence.length();
                        }
                    }
                } else {
                    currentChunk.append(paragraph).append("\n\n");
                    currentLength += paragraph.length();
                }
            } else {
                currentChunk.append(paragraph).append("\n\n");
                currentLength += paragraph.length();
            }
        }

        if (currentLength > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }

    private List<String> splitLongText(String text) {
        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + MAX_CHUNK_SIZE, text.length());
            if (end < text.length()) {
                // 在句子边界处分割
                int lastPeriod = text.lastIndexOf(".", end);
                if (lastPeriod > start + MIN_CHUNK_SIZE) {
                    end = lastPeriod + 1;
                }
            }
            chunks.add(text.substring(start, end).trim());
            start = end;
        }

        return chunks;
    }

    public Map<String, String> extractMetadata(String text) {
        Map<String, String> metadata = new HashMap<>();

        // 1. 检测内容特征
        metadata.put("contains_tables", String.valueOf(containsTables(text)));
        metadata.put("contains_images", String.valueOf(containsImages(text)));
        metadata.put("contains_code", String.valueOf(containsCode(text)));

        // 2. 提取关键词
        metadata.put("keywords", extractKeywords(text));

        // 3. 提取时间信息
        metadata.put("processing_time", LocalDateTime.now().toString());

        return metadata;
    }

    private boolean containsTables(String text) {
        return text.contains("|") || text.contains("+") || text.contains("-");
    }

    private boolean containsImages(String text) {
        return text.toLowerCase().contains("image") ||
               text.toLowerCase().contains("figure") ||
               text.toLowerCase().contains("picture");
    }

    private boolean containsCode(String text) {
        return text.contains("{") || text.contains("}") ||
               text.contains(";") || text.contains("//");
    }

    private String extractKeywords(String text) {
        // 简单的关键词提取实现
        List<String> words = Arrays.asList(text.toLowerCase().split("\\s+"));
        Map<String, Integer> wordCount = new HashMap<>();

        for (String word : words) {
            if (word.length() > 3) { // 忽略短词
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }

        return wordCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));
    }
}
