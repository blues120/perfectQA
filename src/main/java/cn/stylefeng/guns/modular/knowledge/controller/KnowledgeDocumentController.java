package cn.stylefeng.guns.modular.knowledge.controller;

import cn.stylefeng.guns.modular.knowledge.entity.KnowledgeDocument;
import cn.stylefeng.guns.modular.knowledge.model.ApiResponse;
import cn.stylefeng.guns.modular.knowledge.service.KnowledgeDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 知识文档控制器
 */
@RestController
@RequestMapping("/knowledge/document")
@Validated
public class KnowledgeDocumentController {

    @Autowired
    private KnowledgeDocumentService knowledgeDocumentService;

    /**
     * 上传文档
     */
    @PostMapping("/upload")
    public ApiResponse<Boolean> upload(@RequestParam("file") MultipartFile file,
                                     @RequestParam("knowledgeBaseId") String knowledgeBaseId) {
        try {
            Boolean result = knowledgeDocumentService.uploadDocument(file, knowledgeBaseId);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("上传文档失败：" + e.getMessage());
        }
    }

    /**
     * 批量上传文档
     */
    @PostMapping("/upload/batch")
    public ApiResponse<Boolean> uploadBatch(@RequestParam("files") List<MultipartFile> files,
                                          @RequestParam("knowledgeBaseId") String knowledgeBaseId) {
        try {
            Boolean result = knowledgeDocumentService.uploadDocuments(files, knowledgeBaseId);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("批量上传文档失败：" + e.getMessage());
        }
    }

    /**
     * 获取文档列表
     */
    @GetMapping("/list")
    public ApiResponse<List<KnowledgeDocument>> list(@RequestParam("knowledgeBaseId") String knowledgeBaseId) {
        try {
            List<KnowledgeDocument> list = knowledgeDocumentService.listByKnowledgeBaseId(Long.parseLong(knowledgeBaseId));
            return ApiResponse.success(list);
        } catch (Exception e) {
            return ApiResponse.error("获取文档列表失败：" + e.getMessage());
        }
    }

    /**
     * 删除文档
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable String id) {
        try {
            Boolean result = knowledgeDocumentService.deleteDocument(Long.parseLong(id));
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("删除文档失败：" + e.getMessage());
        }
    }

    /**
     * 更新文档
     */
    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody @Validated KnowledgeDocument document) {
        try {
            Boolean result = knowledgeDocumentService.updateDocument(document);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("更新文档失败：" + e.getMessage());
        }
    }

    /**
     * 关键词搜索
     */
    @PostMapping("/search")
    public ApiResponse<List<KnowledgeDocument>> search(@RequestBody Map<String, Object> params) {
        try {
            List<KnowledgeDocument> result = knowledgeDocumentService.searchByKeyword(params);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("搜索文档失败：" + e.getMessage());
        }
    }

    /**
     * 获取文档预览
     */
    @GetMapping("/preview/{id}")
    public ApiResponse<String> preview(@PathVariable String id) {
        try {
            String preview = knowledgeDocumentService.getDocumentPreview(id);
            return ApiResponse.success(preview);
        } catch (Exception e) {
            return ApiResponse.error("获取文档预览失败：" + e.getMessage());
        }
    }
} 