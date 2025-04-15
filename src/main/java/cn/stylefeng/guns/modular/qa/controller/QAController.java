package cn.stylefeng.guns.modular.qa.controller;

import cn.stylefeng.guns.modular.model.model.ApiResponse;
import cn.stylefeng.guns.modular.qa.entity.QA;
import cn.stylefeng.guns.modular.qa.service.QAService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/qa")
public class QAController {

    @Autowired
    private QAService qaService;

    @PostMapping
    public ApiResponse<QA> createQA(@Valid @RequestBody QA qa) {
        try {
            QA createdQA = qaService.createQA(qa);
            return ApiResponse.success(createdQA);
        } catch (Exception e) {
            return ApiResponse.error("创建问答失败：" + e.getMessage());
        }
    }

    @PostMapping("/batch-import")
    public ApiResponse<?> batchImportQAs(@RequestParam("file") MultipartFile file) {
        try {
            qaService.batchImportQAs(file);
            return ApiResponse.success("批量导入成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error("文件格式错误：" + e.getMessage());
        } catch (IOException e) {
            return ApiResponse.error("文件读取失败：" + e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("批量导入失败：" + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ApiResponse<Page<QA>> searchQAs(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {
        try {
            Page<QA> page = qaService.searchQAs(current, size, district, department, keyword, startTime, endTime);
            return ApiResponse.success(page);
        } catch (Exception e) {
            return ApiResponse.error("搜索问答失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteQA(@PathVariable Long id) {
        try {
            boolean success = qaService.deleteQA(id);
            return success ? ApiResponse.success("删除成功") : ApiResponse.error("删除失败");
        } catch (Exception e) {
            return ApiResponse.error("删除问答失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<QA> updateQA(@PathVariable Long id, @Valid @RequestBody QA qa) {
        try {
            QA updatedQA = qaService.updateQA(id, qa);
            return ApiResponse.success(updatedQA);
        } catch (Exception e) {
            return ApiResponse.error("更新问答失败：" + e.getMessage());
        }
    }

    @GetMapping("/similar-questions")
    public ApiResponse<List<String>> generateSimilarQuestions(@RequestParam @NotBlank String question) {
        try {
            List<String> similarQuestions = qaService.generateSimilarQuestions(question);
            return ApiResponse.success(similarQuestions);
        } catch (Exception e) {
            return ApiResponse.error("生成相似问题失败：" + e.getMessage());
        }
    }
}
