package cn.stylefeng.guns.modular.knowledge.controller;

import cn.stylefeng.guns.modular.knowledge.entity.KnowledgeBase;
import cn.stylefeng.guns.modular.knowledge.model.ApiResponse;
import cn.stylefeng.guns.modular.knowledge.service.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 知识库管理控制器
 */
@RestController
@RequestMapping("/knowledge/base")
@Validated
public class KnowledgeBaseController {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    /**
     * 创建知识库
     */
    @PostMapping
    public ApiResponse<Boolean> create(@RequestBody @Validated KnowledgeBase knowledgeBase) {
        try {
            Boolean result = knowledgeBaseService.create(knowledgeBase);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("创建知识库失败：" + e.getMessage());
        }
    }

    /**
     * 更新知识库
     */
    @PutMapping
    public ApiResponse<Boolean> update(@RequestBody @Validated KnowledgeBase knowledgeBase) {
        try {
            Boolean result = knowledgeBaseService.update(knowledgeBase);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("更新知识库失败：" + e.getMessage());
        }
    }

    /**
     * 删除知识库
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable String id) {
        try {
            Boolean result = knowledgeBaseService.delete(id);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("删除知识库失败：" + e.getMessage());
        }
    }

    /**
     * 获取知识库详情
     */
    @GetMapping("/{id}")
    public ApiResponse<KnowledgeBase> getById(@PathVariable String id) {
        try {
            KnowledgeBase knowledgeBase = knowledgeBaseService.getById(id);
            return ApiResponse.success(knowledgeBase);
        } catch (Exception e) {
            return ApiResponse.error("获取知识库详情失败：" + e.getMessage());
        }
    }

    /**
     * 获取知识库列表
     */
    @GetMapping("/list")
    public ApiResponse<List<KnowledgeBase>> list() {
        try {
            List<KnowledgeBase> list = knowledgeBaseService.list();
            return ApiResponse.success(list);
        } catch (Exception e) {
            return ApiResponse.error("获取知识库列表失败：" + e.getMessage());
        }
    }

    /**
     * 元数据分类检索
     */
    @PostMapping("/search")
    public ApiResponse<List<KnowledgeBase>> search(@RequestBody Map<String, Object> params) {
        try {
            List<KnowledgeBase> result = knowledgeBaseService.searchByMetadata(params);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("检索知识库失败：" + e.getMessage());
        }
    }
} 