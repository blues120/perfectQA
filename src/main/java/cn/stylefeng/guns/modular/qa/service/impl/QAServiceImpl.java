package cn.stylefeng.guns.modular.qa.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.stylefeng.guns.modular.qa.entity.QA;
import cn.stylefeng.guns.modular.qa.mapper.QAMapper;
import cn.stylefeng.guns.modular.qa.service.QAService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 问答服务实现类
 */
@Service
@Transactional
public class QAServiceImpl extends ServiceImpl<QAMapper, QA> implements QAService {

    private final ChatLanguageModel model;

    public QAServiceImpl() {
        this.model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama2")
                .build();
    }

    @Override
    public Page<QA> findByDistrictAndDepartment(long current, long size, String district, String department) {
        Page<QA> page = new Page<>(current, size);
        return baseMapper.findByDistrictAndDepartment(page, district, department);
    }

    @Override
    public Page<QA> findByKeywords(long current, long size, String keyword) {
        Page<QA> page = new Page<>(current, size);
        return baseMapper.findByKeywords(page, keyword);
    }

    @Override
    public Page<QA> findByCreateTimeBetween(long current, long size, LocalDateTime startTime, LocalDateTime endTime) {
        Page<QA> page = new Page<>(current, size);
        return baseMapper.findByCreateTimeBetween(page, startTime, endTime);
    }

    @Override
    public Page<QA> searchQAs(long current, long size, String district, String department,
                             String keyword, LocalDateTime startTime, LocalDateTime endTime) {
        Page<QA> page = new Page<>(current, size);
        return baseMapper.searchQAs(page, district, department, keyword, startTime, endTime);
    }

    @Override
    public QA createQA(QA qa) {
        qa.setCreateTime(LocalDateTime.now());
        qa.setUpdateTime(LocalDateTime.now());
        qa.setIsActive(true);
        save(qa);
        return qa;
    }

    @Override
    public QA updateQA(Long id, QA qa) {
        QA existingQA = getById(id);
        if (existingQA == null) {
            throw new RuntimeException("问答不存在：" + id);
        }

        existingQA.setQuestion(qa.getQuestion());
        existingQA.setAnswer(qa.getAnswer());
        existingQA.setDistrict(qa.getDistrict());
        existingQA.setDepartment(qa.getDepartment());
        existingQA.setKeywords(qa.getKeywords());
        existingQA.setUpdateTime(LocalDateTime.now());

        updateById(existingQA);
        return existingQA;
    }

    @Override
    public boolean deleteQA(Long id) {
        return removeById(id);
    }

    @Override
    public List<String> generateSimilarQuestions(String question) {
        String prompt = String.format(
            "基于以下问题，生成3个相似的问题，每个问题都应该表达相同的意思但使用不同的表达方式：\n%s",
            question
        );

        String response = model.generate(prompt);
        List<String> similarQuestions = new ArrayList<>();

        // 简单的分割处理，实际应用中可能需要更复杂的解析
        String[] questions = response.split("\n");
        for (String q : questions) {
            if (q.trim().length() > 0) {
                similarQuestions.add(q.trim());
            }
        }

        return similarQuestions;
    }

    @Override
    @Transactional
    public void batchImportQAs(MultipartFile file) throws IOException {
        if (!isExcelFile(file)) {
            throw new IllegalArgumentException("只支持Excel文件（.xlsx或.xls）");
        }

        List<QA> qas = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // 跳过表头
            int firstRow = sheet.getFirstRowNum() + 1;
            int lastRow = sheet.getLastRowNum();

            for (int i = firstRow; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                QA qa = new QA();
                qa.setQuestion(getCellValue(row.getCell(0)));
                qa.setAnswer(getCellValue(row.getCell(1)));
                qa.setDistrict(getCellValue(row.getCell(2)));
                qa.setDepartment(getCellValue(row.getCell(3)));
                qa.setKeywords(getCellValue(row.getCell(4)));
                qa.setCreateTime(LocalDateTime.now());
                qa.setUpdateTime(LocalDateTime.now());
                qa.setIsActive(true);

                if (qa.getQuestion() != null && qa.getAnswer() != null) {
                    qas.add(qa);
                }
            }
        }

        if (!qas.isEmpty()) {
            saveBatch(qas);
        }
    }

    private boolean isExcelFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return filename != null && (filename.endsWith(".xlsx") || filename.endsWith(".xls"));
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}
