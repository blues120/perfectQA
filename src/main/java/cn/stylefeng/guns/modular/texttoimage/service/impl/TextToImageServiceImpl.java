package cn.stylefeng.guns.modular.texttoimage.service.impl;


import cn.stylefeng.guns.modular.texttoimage.entity.TextToImage;
import cn.stylefeng.guns.modular.texttoimage.mapper.TextToImageMapper;
import cn.stylefeng.guns.modular.texttoimage.service.TextToImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 文本生成图片Service实现类
 */
@Service
@Transactional
public class TextToImageServiceImpl extends ServiceImpl<TextToImageMapper, TextToImage> implements TextToImageService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${text-to-image.stable-diffusion.api-key}")
    private String stableDiffusionApiKey;

    @Value("${text-to-image.dall-e.api-key}")
    private String dallEApiKey;

    @Value("${text-to-image.midjourney.api-key}")
    private String midjourneyApiKey;

    @Override
    public TextToImage createImage(String prompt, String imageUrl, String userId) {
        TextToImage image = new TextToImage();
        image.setPrompt(prompt);
        image.setImageUrl(imageUrl);
        image.setCreateTime(LocalDateTime.now());
        image.setUserId(userId);
        save(image);
        return image;
    }

    @Override
    public List<TextToImage> getUserImages(String userId) {
        return baseMapper.findByUserId(userId);
    }

    @Override
    public String generateImage(String prompt, String model, String userId) {
        String imageData;
        switch (model.toLowerCase()) {
            case "stable-diffusion":
                imageData = generateWithStableDiffusion(prompt);
                break;
            case "dall-e":
                imageData = generateWithDallE(prompt);
                break;
            case "midjourney":
                imageData = generateWithMidjourney(prompt);
                break;
            default:
                throw new IllegalArgumentException("不支持的模型: " + model);
        }

        // 保存记录
        createImage(prompt, "data:image/png;base64," + imageData, userId);

        return imageData;
    }

    private String generateWithStableDiffusion(String prompt) {
        String apiUrl = "https://api.stability.ai/v1/generation/stable-diffusion-v1-6/text-to-image";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(stableDiffusionApiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("text_prompts", List.of(Map.of("text", prompt)));
        requestBody.put("cfg_scale", 7);
        requestBody.put("height", 512);
        requestBody.put("width", 512);
        requestBody.put("steps", 30);
        requestBody.put("samples", 1);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        Map<String, Object> response = restTemplate.postForObject(apiUrl, request, Map.class);

        return (String) ((List<Map<String, Object>>) response.get("artifacts"))
            .get(0).get("base64");
    }

    private String generateWithDallE(String prompt) {
        String apiUrl = "https://api.openai.com/v1/images/generations";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(dallEApiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prompt", prompt);
        requestBody.put("n", 1);
        requestBody.put("size", "512x512");
        requestBody.put("response_format", "b64_json");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        Map<String, Object> response = restTemplate.postForObject(apiUrl, request, Map.class);

        return (String) ((List<Map<String, Object>>) response.get("data"))
            .get(0).get("b64_json");
    }

    private String generateWithMidjourney(String prompt) {
        // 这里只是示例，实际实现需要根据Midjourney API的文档
        return "midjourney_image_data";
    }
}
