package cn.stylefeng.guns.modular.file.controller;

import cn.stylefeng.guns.modular.document.entity.Document;
import cn.stylefeng.guns.modular.file.service.FileProcessingService;

import cn.stylefeng.roses.kernel.rule.pojo.response.ErrorResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import dev.langchain4j.data.segment.TextSegment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
public class FileProcessingController {

    @Autowired
    private FileProcessingService fileProcessingService;

    @PostMapping("/process")
    public ResponseData<Document> processFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam String district,
            @RequestParam String department,
            @RequestParam(required = false) String keywords) {
        try {
            Document document = fileProcessingService.processFile(file, district, department, keywords);
            return new SuccessResponseData<>(document);
        } catch (Exception e) {
            return new ErrorResponseData<>("408",null);
        }
    }

    @PostMapping("/search")
    public ResponseData<List<TextSegment>> searchSimilarSegments(
            @RequestParam String query,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String department) {
        try {
            List<TextSegment> segments = fileProcessingService.searchSimilarSegments(query, district, department);
            return new SuccessResponseData<>(segments);
        } catch (Exception e) {
            return new ErrorResponseData<>("408",null);
        }
    }
}
