package cn.stylefeng.guns.modular.document.controller;

import cn.stylefeng.roses.kernel.rule.pojo.response.ErrorResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.guns.modular.document.entity.Document;
import cn.stylefeng.guns.modular.document.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文档控制器
 */
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    /**
     * 分页获取文档列表
     */
    @GetMapping("/page")
    public ResponseData<Page<Document>> page(
            @RequestParam(value = "current", defaultValue = "1") long current,
            @RequestParam(value = "size", defaultValue = "10") long size) {
        return new SuccessResponseData<>(documentService.page(current, size));
    }

    /**
     * 高级搜索
     */
    @GetMapping("/search")
    public ResponseData<Page<Document>> search(
            @RequestParam(value = "current", defaultValue = "1") long current,
            @RequestParam(value = "size", defaultValue = "10") long size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "district", required = false) String district,
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return new SuccessResponseData<>(
                documentService.searchDocuments(current, size, keyword, district, department, startTime, endTime)
        );
    }

    /**
     * 获取文档详情
     */
    @GetMapping("/{id}")
    public ResponseData<Document> detail(@PathVariable("id") Long id) {
        return new SuccessResponseData<>(documentService.getById(id));
    }

    /**
     * 获取文档内容
     */
    @GetMapping("/{id}/content")
    public ResponseData<String> content(@PathVariable("id") Long id) {
        return new SuccessResponseData<>(documentService.getDocumentContent(id));
    }

    /**
     * 获取与关键词相关的文档
     */
    @GetMapping("/keyword")
    public ResponseData<List<Document>> findByKeyword(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return new SuccessResponseData<>(documentService.findByKeyword(keyword, limit));
    }

    /**
     * 获取用户的文档
     */
    @GetMapping("/user/{createUser}")
    public ResponseData<List<Document>> findByCreateUser(@PathVariable("createUser") String createUser) {
        return new SuccessResponseData<>(documentService.findByCreateUser(createUser));
    }

    /**
     * 获取特定状态的文档
     */
    @GetMapping("/status/{status}")
    public ResponseData<List<Document>> findByStatus(@PathVariable("status") String status) {
        return new SuccessResponseData<>(documentService.findByStatus(status));
    }

    /**
     * 上传文档
     */
    @PostMapping("/upload")
    public ResponseData<Document> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "district", required = false) String district,
            @RequestParam(value = "department", required = false) String department,
            @RequestParam("createUser") String createUser) {
        try {
            Document document = documentService.processDocument(file, district, department, createUser);
            return new SuccessResponseData<>(document);
        } catch (IOException e) {
            return new ErrorResponseData<>("408","上传文档失败: " + e.getMessage());
        }
    }

    /**
     * 更新文档
     */
    @PutMapping("/{id}")
    public ResponseData<Document> update(@PathVariable("id") Long id, @RequestBody Document document) {
        return new SuccessResponseData<>(documentService.updateDocument(id, document));
    }

    /**
     * 更新文档状态
     */
    @PutMapping("/{id}/status")
    public ResponseData<Document> updateStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status) {
        return new SuccessResponseData<>(documentService.updateStatus(id, status));
    }

    /**
     * 删除文档
     */
    @DeleteMapping("/{id}")
    public ResponseData<Boolean> remove(@PathVariable("id") Long id) {
        return new SuccessResponseData<>(documentService.deleteDocument(id));
    }
}
