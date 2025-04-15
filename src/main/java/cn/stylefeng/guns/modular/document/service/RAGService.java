package cn.stylefeng.guns.modular.document.service;

import cn.stylefeng.guns.modular.document.entity.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface RAGService {
    Document processDocument(MultipartFile file, String district, String department) throws IOException;
    
    List<Document> searchDocuments(String query, String district, String department);
    
    void deleteDocument(Long id);
} 