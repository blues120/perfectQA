package cn.stylefeng.guns.modular.texttoimage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.stylefeng.guns.modular.texttoimage.entity.TextToImage;

import java.util.List;

/**
 * 文本生成图片Service接口
 */
public interface TextToImageService extends IService<TextToImage> {

    /**
     * 创建图片记录
     *
     * @param prompt 提示词
     * @param imageUrl 图片URL
     * @param userId 用户ID
     * @return 图片记录
     */
    TextToImage createImage(String prompt, String imageUrl, String userId);

    /**
     * 获取用户的图片记录
     *
     * @param userId 用户ID
     * @return 图片记录列表
     */
    List<TextToImage> getUserImages(String userId);

    /**
     * 生成图片
     *
     * @param prompt 提示词
     * @param model 模型名称
     * @param userId 用户ID
     * @return 图片数据（Base64编码）
     */
    String generateImage(String prompt, String model, String userId);
}
