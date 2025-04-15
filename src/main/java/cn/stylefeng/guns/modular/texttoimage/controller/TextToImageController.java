package cn.stylefeng.guns.modular.texttoimage.controller;

import cn.stylefeng.roses.kernel.rule.pojo.response.ErrorResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.guns.modular.texttoimage.entity.TextToImage;
import cn.stylefeng.guns.modular.texttoimage.service.TextToImageService;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文本生成图片控制器
 */
@RestController
@RequestMapping("/api/text-to-image")
public class TextToImageController {

    @Autowired
    private TextToImageService textToImageService;

    /**
     * 生成图片
     *
     * @param prompt 提示词
     * @param model 模型名称
     * @param userId 用户ID
     * @return 图片数据（Base64编码）
     */
    @PostMapping("/generate")
    public ResponseData<String> generateImage(
            @RequestParam String prompt,
            @RequestParam(defaultValue = "stable-diffusion") String model,
            @RequestParam String userId) {
        try {
            String imageData = textToImageService.generateImage(prompt, model, userId);
            return new SuccessResponseData<>(imageData);
        } catch (Exception e) {
            return new ErrorResponseData<>("408","生成图片失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户的图片记录
     *
     * @param userId 用户ID
     * @return 图片记录列表
     */
    @GetMapping("/user/{userId}")
    public ResponseData<List<TextToImage>> getUserImages(@PathVariable String userId) {
        try {
            List<TextToImage> images = textToImageService.getUserImages(userId);
            return new SuccessResponseData<>(images);
        } catch (Exception e) {
            return new ErrorResponseData<>("获取图片记录失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有可用的模型列表
     *
     * @return 模型列表
     */
    @GetMapping("/models")
    public ResponseData<List<String>> getAvailableModels() {
        List<String> models = List.of("stable-diffusion", "dall-e", "midjourney");
        return new SuccessResponseData<>(models);
    }
}
