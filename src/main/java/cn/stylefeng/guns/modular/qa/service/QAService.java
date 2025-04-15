package cn.stylefeng.guns.modular.qa.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.stylefeng.guns.modular.qa.entity.QA;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 问答服务接口
 */
public interface QAService extends IService<QA> {

    /**
     * 分页查询指定地区和部门的问答
     */
    Page<QA> findByDistrictAndDepartment(long current, long size, String district, String department);

    /**
     * 分页查询包含关键词的问答
     */
    Page<QA> findByKeywords(long current, long size, String keyword);

    /**
     * 分页查询指定时间范围内的问答
     */
    Page<QA> findByCreateTimeBetween(long current, long size, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 高级搜索问答
     */
    Page<QA> searchQAs(
            long current,
            long size,
            String district,
            String department,
            String keyword,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * 创建问答
     */
    QA createQA(QA qa);

    /**
     * 更新问答
     */
    QA updateQA(Long id, QA qa);

    /**
     * 删除问答
     */
    boolean deleteQA(Long id);

    /**
     * 生成相似问题
     */
    List<String> generateSimilarQuestions(String question);

    /**
     * 批量导入问答
     * @param file Excel文件
     * @throws IOException 如果文件读取失败
     */
    void batchImportQAs(MultipartFile file) throws IOException;
}
