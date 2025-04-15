package cn.stylefeng.guns.modular.qa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.stylefeng.guns.modular.qa.entity.QA;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 问答Mapper接口
 */
@Mapper
public interface QAMapper extends BaseMapper<QA> {

    /**
     * 分页查询指定地区和部门的问答
     */
    Page<QA> findByDistrictAndDepartment(
            Page<QA> page,
            @Param("district") String district,
            @Param("department") String department
    );

    /**
     * 分页查询包含关键词的问答
     */
    Page<QA> findByKeywords(
            Page<QA> page,
            @Param("keyword") String keyword
    );

    /**
     * 分页查询指定时间范围内的问答
     */
    Page<QA> findByCreateTimeBetween(
            Page<QA> page,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 高级搜索问答
     */
    Page<QA> searchQAs(
            Page<QA> page,
            @Param("district") String district,
            @Param("department") String department,
            @Param("keyword") String keyword,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
