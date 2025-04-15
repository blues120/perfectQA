package cn.stylefeng.guns.modular.file.service;

import cn.stylefeng.guns.modular.document.entity.Document;
import dev.langchain4j.data.segment.TextSegment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileProcessingService {
    
    /**
     * 处理上传的文件
     * @param file 上传的文件
     * @param district 地区
     * @param department 部门
     * @param keywords 关键词
     * @return 处理后的文档对象
     */
    Document processFile(MultipartFile file, String district, String department, String keywords);
    
    /**
     * 搜索相似段落
     * @param query 查询文本
     * @param district 地区
     * @param department 部门
     * @return 相似段落列表
     */
    List<TextSegment> searchSimilarSegments(String query, String district, String department);
} 