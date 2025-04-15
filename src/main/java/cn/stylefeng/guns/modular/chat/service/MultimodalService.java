package cn.stylefeng.guns.modular.chat.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface MultimodalService {
    
    /**
     * 处理图像
     * @param image 图像文件
     * @return 处理结果
     */
    String processImage(MultipartFile image);
    
    /**
     * 处理音频
     * @param audio 音频文件
     * @return 处理结果
     */
    String processAudio(MultipartFile audio);
    
    /**
     * 处理视频
     * @param video 视频文件
     * @return 处理结果
     */
    String processVideo(MultipartFile video);
    
    /**
     * 图像识别
     * @param image 图像文件
     * @return 识别结果列表
     */
    List<String> recognizeImage(MultipartFile image);
    
    /**
     * 语音识别
     * @param audio 音频文件
     * @return 识别结果
     */
    String recognizeSpeech(MultipartFile audio);
    
    /**
     * 视频分析
     * @param video 视频文件
     * @return 分析结果
     */
    String analyzeVideo(MultipartFile video);
} 