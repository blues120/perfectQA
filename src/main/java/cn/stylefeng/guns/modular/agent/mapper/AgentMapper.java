package cn.stylefeng.guns.modular.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.stylefeng.guns.modular.agent.entity.Agent;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 智能代理Mapper接口
 */
public interface AgentMapper extends BaseMapper<Agent> {

    /**
     * 搜索代理
     *
     * @param page 分页参数
     * @param keyword 关键词
     * @param type 类型
     * @param status 状态
     * @param isEnabled 是否启用
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    Page<Agent> searchAgents(
            Page<Agent> page,
            @Param("keyword") String keyword,
            @Param("type") String type,
            @Param("status") String status,
            @Param("isEnabled") Boolean isEnabled,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 获取启用的代理
     *
     * @return 代理列表
     */
    List<Agent> findEnabledAgents();

    /**
     * 根据类型查询代理
     *
     * @param type 类型
     * @return 代理列表
     */
    List<Agent> findByType(@Param("type") String type);

    /**
     * 根据创建用户查询代理
     *
     * @param createUser 创建用户
     * @return 代理列表
     */
    List<Agent> findByCreateUser(@Param("createUser") String createUser);
}
