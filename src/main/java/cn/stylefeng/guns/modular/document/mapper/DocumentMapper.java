package cn.stylefeng.guns.modular.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.stylefeng.guns.modular.document.entity.Document;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文档Mapper接口
 */
@Mapper
public interface DocumentMapper extends BaseMapper<Document> {

    /**
     * 根据关键词、区域和部门搜索文档
     *
     * @param page 分页参数
     * @param keyword 关键词
     * @param district 区域
     * @param department 部门
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    Page<Document> searchDocuments(
            Page<Document> page,
            @Param("keyword") String keyword,
            @Param("district") String district,
            @Param("department") String department,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 获取与特定关键词相关的文档
     *
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 文档列表
     */
    List<Document> findByKeyword(@Param("keyword") String keyword, @Param("limit") int limit);

    /**
     * 根据创建用户查询文档
     *
     * @param createUser 创建用户
     * @return 文档列表
     */
    List<Document> findByCreateUser(@Param("createUser") String createUser);

    /**
     * 根据状态查询文档
     *
     * @param status 状态
     * @return 文档列表
     */
    List<Document> findByStatus(@Param("status") String status);
}
