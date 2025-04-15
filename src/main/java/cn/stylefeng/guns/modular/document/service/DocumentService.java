package cn.stylefeng.guns.modular.document.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.stylefeng.guns.modular.document.entity.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文档服务接口
 */
public interface DocumentService extends IService<Document> {

    /**
     * 分页查询文档
     *
     * @param current 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    Page<Document> page(long current, long size);

    /**
     * 高级搜索
     *
     * @param current 当前页
     * @param size 每页大小
     * @param keyword 关键词
     * @param district 区域
     * @param department 部门
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    Page<Document> searchDocuments(
            long current,
            long size,
            String keyword,
            String district,
            String department,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * 获取与特定关键词相关的文档
     *
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 文档列表
     */
    List<Document> findByKeyword(String keyword, int limit);

    /**
     * 根据创建用户查询文档
     *
     * @param createUser 创建用户
     * @return 文档列表
     */
    List<Document> findByCreateUser(String createUser);

    /**
     * 根据状态查询文档
     *
     * @param status 状态
     * @return 文档列表
     */
    List<Document> findByStatus(String status);

    /**
     * 上传并处理文档
     *
     * @param file 文件
     * @param district 区域
     * @param department 部门
     * @param createUser 创建用户
     * @return 处理后的文档
     * @throws IOException IO异常
     */
    Document processDocument(MultipartFile file, String district, String department, String createUser) throws IOException;

    /**
     * 更新文档
     *
     * @param id 文档ID
     * @param document 更新信息
     * @return 更新后的文档
     */
    Document updateDocument(Long id, Document document);

    /**
     * 更新文档状态
     *
     * @param id 文档ID
     * @param status 状态
     * @return 更新后的文档
     */
    Document updateStatus(Long id, String status);

    /**
     * 获取文档内容
     *
     * @param id 文档ID
     * @return 文档内容
     */
    String getDocumentContent(Long id);

    /**
     * 删除文档
     *
     * @param id 文档ID
     * @return 是否成功
     */
    boolean deleteDocument(Long id);
}
