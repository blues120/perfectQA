package cn.stylefeng.guns.modular.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.document.entity.Document;
import cn.stylefeng.guns.modular.document.mapper.DocumentMapper;
import cn.stylefeng.guns.modular.document.service.DocumentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 文档服务实现类
 */
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {

    @Override
    public Page<Document> page(long current, long size) {
        Page<Document> page = new Page<>(current, size);
        LambdaQueryWrapper<Document> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Document::getCreateTime);
        return this.page(page, queryWrapper);
    }

    @Override
    public Page<Document> searchDocuments(
            long current,
            long size,
            String keyword,
            String district,
            String department,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        Page<Document> page = new Page<>(current, size);
        return this.baseMapper.searchDocuments(page, keyword, district, department, startTime, endTime);
    }

    @Override
    public List<Document> findByKeyword(String keyword, int limit) {
        return this.baseMapper.findByKeyword(keyword, limit);
    }

    @Override
    public List<Document> findByCreateUser(String createUser) {
        return this.baseMapper.findByCreateUser(createUser);
    }

    @Override
    public List<Document> findByStatus(String status) {
        return this.baseMapper.findByStatus(status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Document processDocument(MultipartFile file, String district, String department, String createUser) throws IOException {
        // 创建保存文件的目录
        Path uploadDir = Paths.get("uploads/documents");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 生成唯一文件名
        String fileId = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();
        String fileName = fileId + "_" + originalFilename;
        Path filePath = uploadDir.resolve(fileName);

        // 保存文件
        Files.copy(file.getInputStream(), filePath);

        // 创建文档记录
        Document document = new Document();
        document.setFileName(fileName);
        document.setFileId(fileId);
        document.setContentType(file.getContentType());
        document.setFilePath(filePath.toString());
        document.setDistrict(district);
        document.setDepartment(department);
        document.setCreateUser(createUser);
        document.setFileSize(file.getSize());
        document.setStatus("processing");
        document.setCreateTime(LocalDateTime.now());
        document.setUpdateTime(LocalDateTime.now());

        // 保存文档记录
        this.save(document);

        // 读取文件内容
        String content = extractContentFromFile(file);
        document.setContent(content);
        document.setStatus("processed");

        // 更新文档记录
        this.updateById(document);

        return document;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Document updateDocument(Long id, Document updatedDocument) {
        Document document = this.getById(id);
        if (document == null) {
            throw new RuntimeException("文档不存在: " + id);
        }

        // 更新文档信息
        if (StringUtils.hasText(updatedDocument.getDistrict())) {
            document.setDistrict(updatedDocument.getDistrict());
        }

        if (StringUtils.hasText(updatedDocument.getDepartment())) {
            document.setDepartment(updatedDocument.getDepartment());
        }

        if (StringUtils.hasText(updatedDocument.getKeywords())) {
            document.setKeywords(updatedDocument.getKeywords());
        }

        if (StringUtils.hasText(updatedDocument.getStatus())) {
            document.setStatus(updatedDocument.getStatus());
        }

        document.setUpdateTime(LocalDateTime.now());

        this.updateById(document);
        return document;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Document updateStatus(Long id, String status) {
        Document document = this.getById(id);
        if (document == null) {
            throw new RuntimeException("文档不存在: " + id);
        }

        document.setStatus(status);
        document.setUpdateTime(LocalDateTime.now());

        this.updateById(document);
        return document;
    }

    @Override
    public String getDocumentContent(Long id) {
        Document document = this.getById(id);
        if (document == null) {
            throw new RuntimeException("文档不存在: " + id);
        }

        return document.getContent();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDocument(Long id) {
        Document document = this.getById(id);
        if (document == null) {
            return false;
        }

        // 删除文档记录
        this.removeById(id);

        // 尝试删除物理文件
        try {
            if (StringUtils.hasText(document.getFilePath())) {
                Path filePath = Paths.get(document.getFilePath());
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            // 记录日志，但不影响操作结果
            log.error("删除文件失败: " + document.getFilePath(), e);
        }

        return true;
    }

    /**
     * 从文件中提取内容
     *
     * @param file 文件
     * @return 内容
     * @throws IOException IO异常
     */
    private String extractContentFromFile(MultipartFile file) throws IOException {
        // 这里只是简单实现，实际项目中可能需要根据不同文件类型进行处理
        // 例如使用Apache Tika或其他库解析PDF、Word等文件

        // 如果是文本文件，直接读取内容
        if (file.getContentType() != null && file.getContentType().startsWith("text/")) {
            return new String(file.getBytes());
        }

        // 对于其他类型的文件，可以返回文件名或使用外部工具提取内容
        return "Content of " + file.getOriginalFilename();
    }
}
